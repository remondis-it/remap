package com.remondis.remap;

import com.googlecode.openbeans.PropertyDescriptor;

import static com.remondis.remap.Properties.asString;
import static com.remondis.remap.ReflectionUtil.getCollector;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * The reassign operation maps a field to another field while the field names may differ. A reassign operation is only
 * allowed on fields of the same type.
 *
 * @author schuettec
 */
public class ReassignTransformation extends Transformation {

  private static final String REASSIGNING_MSG = "Reassigning %s\n           to %s";

  ReassignTransformation(MappingConfiguration<?, ?> mapping, PropertyDescriptor sourceProperty,
      PropertyDescriptor destinationProperty) {
    super(mapping, sourceProperty, destinationProperty);
  }

  protected static boolean isEqualTypes(Class<?> sourceType, Class<?> destinationType) {
    return sourceType.equals(destinationType);
  }

  private boolean isReferenceMapping(Class<?> sourceType, Class<?> destinationType) {
    return isEqualTypes(sourceType, destinationType) || ReflectionUtil.isWrapper(sourceType, destinationType)
        || ReflectionUtil.isWrapper(destinationType, sourceType);
  }

  @Override
  protected void performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty, Object destination) throws MappingException {
    Object sourceValue = readOrFail(sourceProperty, source);
    // Only if the source value is not null we have to perform the mapping
    MappedResult result = MappedResult.skip();
    if (sourceValue != null) {
      result = performValueTransformation(sourceValue, destination);
    }

    if (result.hasValue()) {
      writeOrFail(destinationProperty, destination, result.getValue());
    }
  }

  @Override
  protected MappedResult performValueTransformation(Object source, Object destination) throws MappingException {
    Object destinationValue;
    GenericParameterContext sourceCtx = new GenericParameterContext(sourceProperty.getReadMethod());
    GenericParameterContext destinationCtx = new GenericParameterContext(destinationProperty.getReadMethod());
    destinationValue = _convert(sourceCtx.getCurrentType(), source, destinationCtx.getCurrentType(), destination,
        sourceCtx, destinationCtx);
    return MappedResult.value(destinationValue);
  }

  @SuppressWarnings({
      "unchecked", "rawtypes"
  })
  private Object _convert(Class<?> sourceType, Object sourceValue, Class<?> destinationType, Object destination,
      GenericParameterContext sourceCtx, GenericParameterContext destinationCtx) {
    if (hasMapperFor(sourceType, destinationType)) {
      InternalMapper mapper = getMapperFor(sourceType, destinationType);
      return mapper.map(sourceValue, null);
    } else if (isMap(sourceValue)) {
      return convertMap(sourceValue, sourceCtx, destinationCtx);
    } else if (isCollection(sourceValue)) {
      return convertCollection(sourceValue, sourceCtx, destinationCtx);
    } else {
      return convertValueMapOver(sourceType, sourceValue, destinationType, destination);
    }
  }

  @SuppressWarnings({
      "unchecked", "rawtypes"
  })
  private Object convertCollection(Object sourceValue, GenericParameterContext sourceCtx,
      GenericParameterContext destinationCtx) {
    Class<?> destinationCollectionType = destinationCtx.getCurrentType();
    Collection collection = Collection.class.cast(sourceValue);
    Collector collector = getCollector(destinationCollectionType);
    return collection.stream()
        .map(o -> {
          GenericParameterContext newSourceCtx = sourceCtx.goInto(0);
          Class<?> sourceElementType = newSourceCtx.getCurrentType();
          GenericParameterContext newDestCtx = destinationCtx.goInto(0);
          Class<?> destinationElementType = newDestCtx.getCurrentType();
          return _convert(sourceElementType, o, destinationElementType, null, newSourceCtx, newDestCtx);
        })
        .collect(collector);
  }

  @SuppressWarnings({
      "rawtypes", "unchecked"
  })
  private Object convertMap(Object sourceValue, GenericParameterContext sourceCtx,
      GenericParameterContext destinationCtx) {

    GenericParameterContext sourceKeyContext = sourceCtx.goInto(0);
    Class<?> sourceMapKeyType = sourceKeyContext.getCurrentType();
    GenericParameterContext destKeyContext = destinationCtx.goInto(0);
    Class<?> destinationMapKeyType = destKeyContext.getCurrentType();
    GenericParameterContext sourceValueContext = sourceCtx.goInto(1);
    Class<?> sourceMapValueType = sourceValueContext.getCurrentType();
    GenericParameterContext destValueContext = destinationCtx.goInto(1);
    Class<?> destinationMapValueType = destValueContext.getCurrentType();

    Map<?, ?> map = Map.class.cast(sourceValue);
    return map.entrySet()
        .stream()
        .map(o -> {
          Object key = o.getKey();
          Object value = o.getValue();
          Object mappedKey = _convert(sourceMapKeyType, key, destinationMapKeyType, null, sourceKeyContext,
              destKeyContext);
          Object mappedValue = _convert(sourceMapValueType, value, destinationMapValueType, null, sourceValueContext,
              destValueContext);
          return new AbstractMap.SimpleEntry(mappedKey, mappedValue);
        })
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  @SuppressWarnings({
      "unchecked", "rawtypes"
  })
  Object convertValue(Class<?> sourceType, Object sourceValue, Class<?> destinationType) {
    if (isReferenceMapping(sourceType, destinationType)) {
      return sourceValue;
    } else {
      // Object types must be mapped by a registered mapper before setting the value.
      InternalMapper delegateMapper = getMapperFor(sourceType, destinationType);
      return delegateMapper.map(sourceValue);
    }
  }

  @SuppressWarnings({
      "unchecked", "rawtypes"
  })
  Object convertValueMapOver(Class<?> sourceType, Object sourceValue, Class<?> destinationType,
      Object destinationValue) {
    if (isReferenceMapping(sourceType, destinationType)) {
      return sourceValue;
    } else {
      // Object types must be mapped by a registered mapper before setting the value.
      InternalMapper delegateMapper = getMapperFor(sourceType, destinationType);
      Object destinationValueMapped = readOrFail(destinationProperty, destinationValue);
      return delegateMapper.map(sourceValue, destinationValueMapped);
    }
  }

  /**
   * Finds the generic return type of a method in nested generics. For example this method returns {@link String} when
   * called on a method like <code>List&lt;List&lt;Set&lt;String&gt;&gt;&gt; get();</code>.
   *
   * @param method The method to analyze.
   * @return Returns the inner generic type.
   */
  static Class<?> findGenericTypeFromMethod(Method method, int genericParameterIndex) {
    ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
    Type type = null;
    while (parameterizedType != null) {
      type = parameterizedType.getActualTypeArguments()[genericParameterIndex];
      if (type instanceof ParameterizedType) {
        parameterizedType = (ParameterizedType) type;
      } else if (type instanceof TypeVariable) {
        type = Object.class;
        parameterizedType = null;
      } else {
        parameterizedType = null;
      }
    }
    return (Class<?>) type;
  }

  static boolean isCollection(Class<?> type) {
    return Collection.class.isAssignableFrom(type);
  }

  static boolean isCollection(Object collection) {
    return collection instanceof Collection;
  }

  @Override
  protected void validateTransformation() throws MappingException {
    // we have to check that all required mappers are known for nested mapping
    // if this transformation performs an object mapping, check for known mappers

    GenericParameterContext sourceCtx = new GenericParameterContext(getSourceProperty().getReadMethod());
    GenericParameterContext destCtx = new GenericParameterContext(getDestinationProperty().getReadMethod());

    _validateTransformation(sourceCtx, destCtx);

  }

  private void _validateTransformation(GenericParameterContext sourceCtx, GenericParameterContext destCtx) {
    // Travers nested types here and check for equal map/collection and existing type mapping.
    Class<?> sourceType = sourceCtx.getCurrentType();
    Class<?> destinationType = destCtx.getCurrentType();
    boolean incompatibleCollecion = (isMap(sourceType) && isCollection(destinationType))
        || (isCollection(sourceType) && isMap(destinationType))
        || (noCollectionOrMap(sourceType) && isCollectionOrMap(destinationType))
        || (isCollectionOrMap(sourceType) && noCollectionOrMap(destinationType));

    if (incompatibleCollecion) {
      throw MappingException.incompatibleCollectionMapping(getSourceProperty(), sourceCtx, getDestinationProperty(),
          destCtx);
    }
    if (isMap(sourceType)) {
      GenericParameterContext sourceKeyContext = sourceCtx.goInto(0);
      GenericParameterContext destKeyContext = destCtx.goInto(0);

      GenericParameterContext sourceValueContext = sourceCtx.goInto(1);
      GenericParameterContext destValueContext = destCtx.goInto(1);

      _validateTransformation(sourceKeyContext, destKeyContext);
      _validateTransformation(sourceValueContext, destValueContext);
    }
    if (isCollection(sourceType)) {
      GenericParameterContext sourceElemType = sourceCtx.goInto(0);
      GenericParameterContext destElemType = destCtx.goInto(0);
      _validateTransformation(sourceElemType, destElemType);
    } else {
      validateTypeMapping(getSourceProperty(), sourceType, getDestinationProperty(), destinationType);
    }
  }

  private static boolean noCollectionOrMap(Class<?> type) {
    return !isMap(type) && !isCollection(type);
  }

  private static boolean isCollectionOrMap(Class<?> type) {
    return !noCollectionOrMap(type);
  }

  private void validateTypeMapping(PropertyDescriptor sourceProperty, Class<?> sourceType,
      PropertyDescriptor destinationProperty, Class<?> destinationType) {

    if (!isReferenceMapping(sourceType, destinationType)) {
      // Check if there is a registered mapper if required.
      getMapperFor(sourceType, destinationType);
    }
  }

  private static boolean isMap(Class<?> sourceType) {
    return Map.class.isAssignableFrom(sourceType);
  }

  private static boolean isMap(Object object) {
    return (object instanceof Map);
  }

  @Override
  public String toString(boolean detailed) {
    return String.format(REASSIGNING_MSG, asString(sourceProperty, detailed), asString(destinationProperty, detailed));

  }

}
