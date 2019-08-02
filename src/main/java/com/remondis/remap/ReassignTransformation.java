package com.remondis.remap;

import static com.remondis.remap.Properties.asString;
import static com.remondis.remap.ReflectionUtil.getCollector;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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

  ReassignTransformation(Mapping<?, ?> mapping, PropertyDescriptor sourceProperty,
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
    if (sourceValue != null) {
      Object destinationValue = _convert(getSourceType(), sourceValue, getDestinationType(), destination, 0);
      writeOrFail(destinationProperty, destination, destinationValue);
    }
  }

  private Object _convert(Class<?> sourceType, Object sourceValue, Class<?> destinationType, Object destination,
      int genericParameterDepth) {
    Object destinationValue;
    if (hasMapperFor(sourceType, destinationType)) {
      InternalMapper mapper = getMapperFor(this.sourceProperty, sourceType, this.destinationProperty, destinationType);
      destinationValue = mapper.map(sourceValue, null);
    } else if (isMap(sourceValue)) {
      return convertMap(sourceValue, genericParameterDepth);
    } else if (isCollection(sourceValue)) {
      destinationValue = convertCollection(sourceValue, genericParameterDepth);
    } else {
      destinationValue = convertValueMapOver(sourceType, sourceValue, destinationType, destination);
    }
    return destinationValue;
  }

  private Object convertCollection(Object sourceValue, int genericParameterDepth) {
    Class<?> sourceCollectionType = findGenericTypeFromMethod(this.sourceProperty.getReadMethod(),
        genericParameterDepth, 0);
    Class<?> destinationCollectionType = findGenericTypeFromMethod(this.destinationProperty.getReadMethod(),
        genericParameterDepth, 0);
    Collection collection = Collection.class.cast(sourceValue);
    Collector collector = getCollector(destinationCollectionType);
    return collection.stream()
        .map(o -> {
          Class<?> sourceElementType = findGenericTypeFromMethod(this.sourceProperty.getReadMethod(),
              genericParameterDepth + 1, 0);
          Class<?> destinationElementType = findGenericTypeFromMethod(this.destinationProperty.getReadMethod(),
              genericParameterDepth + 1, 0);

          return _convert(sourceElementType, o, destinationElementType, null, genericParameterDepth + 1);
        })
        .collect(collector);
  }

  private Object convertMap(Object sourceValue, int genericParameterDepth) {
    Class<?> sourceMapKeyType = findGenericTypeFromMethod(sourceProperty.getReadMethod(), genericParameterDepth + 1, 0);
    Class<?> destinationMapKeyType = findGenericTypeFromMethod(destinationProperty.getReadMethod(),
        genericParameterDepth + 1, 0);
    Class<?> sourceMapValueType = findGenericTypeFromMethod(sourceProperty.getReadMethod(), genericParameterDepth + 1,
        1);
    Class<?> destinationMapValueType = findGenericTypeFromMethod(destinationProperty.getReadMethod(),
        genericParameterDepth + 1, 1);
    Map<?, ?> map = Map.class.cast(sourceValue);
    return map.entrySet()
        .stream()
        .map(o -> {
          Object key = o.getKey();
          Object value = o.getValue();
          Object mappedKey = _convert(sourceMapKeyType, key, destinationMapKeyType, null, genericParameterDepth);
          Object mappedValue = _convert(sourceMapValueType, value, destinationMapValueType, null,
              genericParameterDepth);
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
      InternalMapper delegateMapper = getMapperFor(sourceProperty, sourceType, destinationProperty, destinationType);
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
      InternalMapper delegateMapper = getMapperFor(sourceProperty, sourceType, destinationProperty, destinationType);
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
      } else {
        parameterizedType = null;
      }
    }
    return (Class<?>) type;
  }

  /**
   * Finds the generic return type of a method in nested generics. For example this method returns {@link String} when
   * called on a method like <code>List&lt;List&lt;Set&lt;String&gt;&gt;&gt; get();</code>.
   *
   * @param method The method to analyze.
   * @return Returns the inner generic type.
   */
  static Class<?> findGenericTypeFromMethod(Method method, int genericParameterDepth, int genericParameterIndex) {
    ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
    if (genericParameterDepth == 0) {
      return (Class<?>) parameterizedType.getRawType();
    }
    Type type = null;
    int i = 1;
    while (parameterizedType != null && i <= genericParameterDepth) {
      type = parameterizedType.getActualTypeArguments()[genericParameterIndex];
      if (type instanceof ParameterizedType) {
        parameterizedType = (ParameterizedType) type;
      } else {
        parameterizedType = null;
      }
      i++;
    }
    if (parameterizedType == null) {
      return (Class<?>) type;
    } else {
      return (Class<?>) parameterizedType.getRawType();
    }
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
    Class<?> sourceType = getSourceType();
    if (isMap(sourceType)) {
      Class<?> sourceMapKeyType = findGenericTypeFromMethod(sourceProperty.getReadMethod(), 0);
      Class<?> destinationMapKeyType = findGenericTypeFromMethod(destinationProperty.getReadMethod(), 0);
      Class<?> sourceMapValueType = findGenericTypeFromMethod(sourceProperty.getReadMethod(), 1);
      Class<?> destinationMapValueType = findGenericTypeFromMethod(destinationProperty.getReadMethod(), 1);
      validateTypeMapping(getSourceProperty(), sourceMapKeyType, getDestinationProperty(), destinationMapKeyType);
      validateTypeMapping(getSourceProperty(), sourceMapValueType, getDestinationProperty(), destinationMapValueType);
    }
    if (isCollection(sourceType)) {
      Class<?> sourceCollectionType = findGenericTypeFromMethod(sourceProperty.getReadMethod(), 0);
      Class<?> destinationCollectionType = findGenericTypeFromMethod(destinationProperty.getReadMethod(), 0);
      validateTypeMapping(getSourceProperty(), sourceCollectionType, getDestinationProperty(),
          destinationCollectionType);
    } else {
      Class<?> destinationType = getDestinationType();
      validateTypeMapping(getSourceProperty(), sourceType, getDestinationProperty(), destinationType);
    }
  }

  private void validateTypeMapping(PropertyDescriptor sourceProperty, Class<?> sourceType,
      PropertyDescriptor destinationProperty, Class<?> destinationType) {

    if (!isReferenceMapping(sourceType, destinationType)) {
      // Check if there is a registered mapper if required.
      getMapperFor(sourceProperty, sourceType, destinationProperty, destinationType);
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
