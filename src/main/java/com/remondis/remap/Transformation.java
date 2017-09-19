package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;
import static com.remondis.remap.ReflectionUtil.isBuildInType;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import lombok.EqualsAndHashCode;

/**
 * This is the base class for a transformation that performs a single step when mapping from an object to another
 * object. There will be different implementations for mapping operations.
 *
 *
 * @author schuettec
 *
 */
@EqualsAndHashCode(exclude = "mapping")
abstract class Transformation {

  protected PropertyDescriptor sourceProperty;
  protected PropertyDescriptor destinationProperty;
  private Mapping<?, ?> mapping;

  Transformation(Mapping<?, ?> mapping, PropertyDescriptor sourceProperty, PropertyDescriptor destinationProperty) {
    super();
    denyNull("mapping", mapping);
    this.mapping = mapping;
    this.sourceProperty = sourceProperty;
    this.destinationProperty = destinationProperty;
  }

  /**
   * This method throws a {@link MappingException} if the source and destination types of this transformation are not
   * equal.
   *
   * @param sourceType
   *          The source type
   * @param destinationType
   *          The destination type
   */
  protected void denyDifferentPrimitiveTypes(Class<?> sourceType, Class<?> destinationType) {
    // We can check here for !destinationType.isAssignableFrom(sourceType) but this would result in type casts and this
    // must be tested with every client using the mapper to assert this intention. To avoid the need of tests, we only
    // allow type mappings on exactly equal classes.
    /*
     * Fail if
     * a) a primitive type is mapped to object or
     * b) both are primitives but of different types.
     */
    if (isPrimitiveToObjectMapping(sourceType, destinationType)
        || (isReferenceMapping(sourceType, destinationType) && !isEqualTypes(sourceType, destinationType))) {
      throw MappingException.incompatiblePropertyTypes(this, sourceProperty, destinationProperty);
    }
  }

  protected boolean isEqualTypes(Class<?> sourceType, Class<?> destinationType) {
    return sourceType.equals(destinationType);
  }

  protected Class<?> getDestinationType() {
    return destinationProperty.getPropertyType();
  }

  protected Class<?> getSourceType() {
    return sourceProperty.getPropertyType();
  }

  /**
   * Checks if the specified mapping is a valid reference mapping. A reference mapping should be chosen if
   * types are equal and
   * <ul>
   * <li>Java primitives</li>
   * <li>or Java build-in type such as {@link Integer} or {@link String}</li>
   * <li>or if enum values are to be mapped.</li>
   * </ul>
   *
   * @param sourceType
   *          The source type
   * @param destinationType
   *          The destination type
   * @return Returns <code>true</code> if both types equal and Java primitives, otherwise <code>false</code> is
   *         returned.
   */
  protected boolean isReferenceMapping(Class<?> sourceType, Class<?> destinationType) {
    return ((sourceType.isPrimitive() && destinationType.isPrimitive())
        || (isBuildInType(sourceType) && isBuildInType(destinationType)))
        || ((isEnumType(sourceType) && isEnumType(destinationType))) && isEqualTypes(sourceType, destinationType);
  }

  private boolean isEnumType(Class<?> type) {
    return type.isEnum();
  }

  protected boolean isPrimitiveToObjectMapping(Class<?> sourceType, Class<?> destinationType) {
    return sourceType.isPrimitive() ^ destinationType.isPrimitive();
  }

  protected Object readOrFail(PropertyDescriptor property, Object source) {
    try {
      Method readMethod = property.getReadMethod();
      readMethod.setAccessible(true);
      return readMethod.invoke(source);
    } catch (InvocationTargetException e) {
      throw MappingException.invocationTarget(property, e);
    } catch (Exception e) {
      throw MappingException.invocationFailed(property, e);
    }
  }

  protected void writeOrFail(PropertyDescriptor property, Object source, Object value) {
    try {
      Method writeMethod = property.getWriteMethod();
      writeMethod.setAccessible(true);
      writeMethod.invoke(source, value);
    } catch (InvocationTargetException e) {
      throw MappingException.invocationTarget(property, e);
    } catch (Exception e) {
      throw MappingException.invocationFailed(property, e);
    }
  }

  /**
   * Performs the transformation for the specified source and destinatione.
   *
   * @param source
   *          The source object
   * @param destination
   *          The destination object.
   * @throws MappingException
   *           Thrown on any transformation error.
   */
  public void performTransformation(Object source, Object destination) throws MappingException {
    performTransformation(sourceProperty, source, destinationProperty, destination);
  }

  /**
   * Performs a single transformation step while mapping.
   *
   * @param sourceProperty
   *          The source property
   * @param source
   *          The source object to map from.
   * @param destinationProperty
   *          The destination property
   * @param destination
   *          The destination object to map to.
   * @throws MappingException
   *           Thrown on any mapping exception.
   */
  protected abstract void performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty, Object destination) throws MappingException;

  /**
   * Lets this transformation validate its configuration. If the state of this transformation is invalid,
   * implementations may throw a {@link MappingException}.
   *
   * @throws MappingException
   *           Thrown if the transformation setup is invalid
   */
  protected abstract void validateTransformation() throws MappingException;

  /**
   * Returns a mapper to map the specified source type to the specified destination type when registered.
   *
   * @param sourceType
   *          The source type
   * @param destinationType
   *          The destination type
   * @return Returns a mapper for the specified mapping if one was registered. Otherwise a {@link MappingException} is
   *         thrown.
   */
  <S, T> Mapper<S, T> getMapperFor(Class<S> sourceType, Class<T> destinationType) {
    return this.mapping.getMapperFor(sourceType, destinationType);
  }

  PropertyDescriptor getSourceProperty() {
    return sourceProperty;
  }

  PropertyDescriptor getDestinationProperty() {
    return destinationProperty;
  }

}
