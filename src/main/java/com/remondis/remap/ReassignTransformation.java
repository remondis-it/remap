package com.remondis.remap;

import static com.remondis.remap.Properties.asString;
import static com.remondis.remap.ReflectionUtil.getCollector;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collector;

/**
 * The reassing operation maps a field to another field while the field names may differ. A reassign operation is only
 * allowed on fields of the same type.
 *
 * @author schuettec
 */
public class ReassignTransformation extends Transformation {

  private static final String REASSIGNING_MSG = "Reassigning %s\n           to %s";

  ReassignTransformation(Mapping<?, ?> mapping, PropertyDescriptor sourceProperty,
      PropertyDescriptor destinationProperty) {
    super(mapping, sourceProperty, destinationProperty);
    denyDifferentPrimitiveTypes(getSourceType(), getDestinationType());
  }

  @Override
  protected void performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty, Object destination) throws MappingException {
    Object sourceValue = readOrFail(sourceProperty, source);
    // Only if the source value is not null we have to perform the mapping
    if (sourceValue != null) {
      Object destinationValue = null;

      Class<?> sourceType = getSourceType();
      Class<?> destinationType = getDestinationType();

      // Primitive types can be set without any conversion, because we checked type
      // compatibility before.
      if (isCollection(sourceType)) {
        Class<?> sourceCollectionType = findGenericTypeFromMethod(sourceProperty.getReadMethod());
        Class<?> destinationCollectionType = findGenericTypeFromMethod(destinationProperty.getReadMethod());
        destinationValue = convertCollection(sourceValue, sourceCollectionType, destinationCollectionType);
      } else {
        destinationValue = convertValue(sourceValue, sourceType, destination, destinationType);
      }

      writeOrFail(destinationProperty, destination, destinationValue);
    }
  }

  @SuppressWarnings({
      "unchecked", "rawtypes"
  })
  private Object convertCollection(Object sourceValue, Class<?> sourceCollectionType,
                                   Class<?> destinationCollectionType) {
    Collection collection = Collection.class.cast(sourceValue);
    Collector collector = getCollector(collection);
    return collection.stream()
        .map(o -> {
          if (isCollection(o)) {
            return convertCollection(o, sourceCollectionType, destinationCollectionType);
          } else {
            return convertValue(o, sourceCollectionType, destinationCollectionType);
          }
        })
        .collect(collector);
  }

  @SuppressWarnings({
      "unchecked", "rawtypes"
  })
  Object convertValue(Object sourceValue, Class<?> sourceType, Class<?> destinationType) {
    if (isReferenceMapping(sourceType, destinationType) || isEqualTypes(sourceType, destinationType)) {
      return sourceValue;
    } else {
      // Object types must be mapped by a registered mapper before setting the value.
      Mapper delegateMapper = getMapperFor(sourceType, destinationType);
      return delegateMapper.map(sourceValue);
    }
  }

  @SuppressWarnings({
      "unchecked", "rawtypes"
  })
  Object convertValue(Object sourceValue, Class<?> sourceType, Object destinationValue, Class<?> destinationType) {
    if (isReferenceMapping(sourceType, destinationType) || isEqualTypes(sourceType, destinationType)) {
      return sourceValue;
    } else {
      // Object types must be mapped by a registered mapper before setting the value.
      Mapper delegateMapper = getMapperFor(sourceType, destinationType);
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
  static Class<?> findGenericTypeFromMethod(Method method) {
    ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
    Type type = null;
    while (parameterizedType != null) {
      type = parameterizedType.getActualTypeArguments()[0];
      if (type instanceof ParameterizedType) {
        parameterizedType = (ParameterizedType) type;
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
    // we have to check that all needed mappers are known for nested mapping
    // if this transformation performes an object mapping, check for known mappers
    Class<?> sourceType = getSourceType();
    if (isMap(sourceType)) {
      throw MappingException.denyReassignOnMaps(getSourceProperty(), getDestinationProperty());
    }
    if (isCollection(sourceType)) {
      Class<?> sourceCollectionType = findGenericTypeFromMethod(sourceProperty.getReadMethod());
      Class<?> destinationCollectionType = findGenericTypeFromMethod(destinationProperty.getReadMethod());
      validateTypeMapping(sourceCollectionType, destinationCollectionType);
    } else {
      Class<?> destinationType = getDestinationType();
      validateTypeMapping(sourceType, destinationType);
    }
  }

  private void validateTypeMapping(Class<?> sourceType, Class<?> destinationType) {
    if (!(isReferenceMapping(sourceType, destinationType) || isEqualTypes(sourceType, destinationType))) {
      getMapperFor(sourceType, destinationType);
    }
  }

  private boolean isMap(Class<?> sourceType) {
    return Map.class.isAssignableFrom(sourceType);
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return String.format(REASSIGNING_MSG, asString(sourceProperty), asString(destinationProperty));
  }
}
