package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;
import static java.util.Objects.isNull;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jspecify.annotations.Nullable;

/**
 * This is the base class for a transformation that performs a single step when mapping from an object to another
 * object. There will be different implementations for mapping operations.
 *
 * @author schuettec
 */
abstract class Transformation {

  /**
   * The source property of this transformation. This is <code>null</code> for transformations that do not read a
   * source property, like the <code>set</code> operation.
   */
  protected @Nullable PropertyDescriptor sourceProperty;
  /**
   * The destination property of this transformation. This is <code>null</code> for transformations that do not write a
   * destination property, like the <code>omitInSource</code> operation.
   */
  protected @Nullable PropertyDescriptor destinationProperty;
  protected MappingConfiguration<?, ?> mapping;

  /*
   * The accessible read/write methods are resolved once at configuration time because setAccessible is expensive and
   * must not be called for every mapping operation. The Method instances are cached here since PropertyDescriptor
   * holds them softly-referenced and may return fresh instances without the accessible flag set.
   */
  private final @Nullable Method sourceReadMethod;
  private final @Nullable Method destinationReadMethod;
  private final @Nullable Method destinationWriteMethod;

  Transformation(MappingConfiguration<?, ?> mapping, @Nullable PropertyDescriptor sourceProperty,
      @Nullable PropertyDescriptor destinationProperty) {
    super();
    denyNull("mapping", mapping);
    this.mapping = mapping;
    this.sourceProperty = sourceProperty;
    this.destinationProperty = destinationProperty;
    this.sourceReadMethod = accessibleOrNull(sourceProperty == null ? null : sourceProperty.getReadMethod());
    this.destinationReadMethod = accessibleOrNull(
        destinationProperty == null ? null : destinationProperty.getReadMethod());
    this.destinationWriteMethod = accessibleOrNull(
        destinationProperty == null ? null : destinationProperty.getWriteMethod());
  }

  /**
   * Makes the specified method accessible on a best-effort basis. If the method cannot be made accessible, the
   * original method is returned and the access error surfaces on invocation like before.
   */
  private static @Nullable Method accessibleOrNull(@Nullable Method method) {
    if (method == null) {
      return null;
    }
    try {
      method.setAccessible(true);
    } catch (RuntimeException e) {
      // Ignore here: invoking the inaccessible method throws an IllegalAccessException which is reported as
      // MappingException by readOrFail/writeOrFail.
    }
    return method;
  }

  /**
   * Returns the cached accessible read method for the specified property. Falls back to resolving the method from the
   * property descriptor if an unknown descriptor is passed.
   */
  private Method readMethodOf(PropertyDescriptor property) {
    if (property == sourceProperty && sourceReadMethod != null) {
      return sourceReadMethod;
    }
    if (property == destinationProperty && destinationReadMethod != null) {
      return destinationReadMethod;
    }
    Method readMethod = property.getReadMethod();
    readMethod.setAccessible(true);
    return readMethod;
  }

  /**
   * Returns the cached accessible write method for the specified property. Falls back to resolving the method from
   * the property descriptor if an unknown descriptor is passed.
   */
  private Method writeMethodOf(PropertyDescriptor property) {
    if (property == destinationProperty && destinationWriteMethod != null) {
      return destinationWriteMethod;
    }
    Method writeMethod = property.getWriteMethod();
    writeMethod.setAccessible(true);
    return writeMethod;
  }

  /**
   * @return Returns the destination property name.
   */
  public @Nullable String getDestinationPropertyName() {
    return isNull(destinationProperty) ? null : destinationProperty.getName();
  }

  /**
   * @return Returns the source property name. This might be a property path, if the transformation operates on
   *         property paths like PropertyPathTransformation.
   */
  public @Nullable String getSourcePropertyName() {
    return isNull(sourceProperty) ? null : sourceProperty.getName();
  }

  protected Class<?> getDestinationType() {
    return destinationProperty.getPropertyType();
  }

  protected Class<?> getSourceType() {
    return sourceProperty.getPropertyType();
  }

  protected @Nullable Object readOrFail(PropertyDescriptor property, Object source) {
    try {
      return readMethodOf(property).invoke(source);
    } catch (InvocationTargetException e) {
      throw MappingException.invocationTarget(property, e);
    } catch (Exception e) {
      throw MappingException.invocationFailed(property, e);
    }
  }

  protected void writeOrFail(PropertyDescriptor property, Object source, @Nullable Object value) {
    try {
      writeMethodOf(property).invoke(source, value);
    } catch (InvocationTargetException e) {
      throw MappingException.invocationTarget(property, e);
    } catch (Exception e) {
      throw MappingException.invocationFailed(property, e);
    }
  }

  /**
   * Performs the transformation for the specified source and destination.
   *
   * @param source The source object
   * @param destination The destination object.
   * @throws MappingException Thrown on any transformation error.
   */
  public void performTransformation(Object source, Object destination) throws MappingException {
    performTransformation(sourceProperty, source, destinationProperty, destination);
  }

  /**
   * Performs a single transformation step while mapping.
   *
   * @param sourceProperty The source property
   * @param source The source object to map from.
   * @param destinationProperty The destination property
   * @param destination The destination object to map to.
   * @throws MappingException Thrown on any mapping exception.
   */
  protected abstract void performTransformation(@Nullable PropertyDescriptor sourceProperty, Object source,
      @Nullable PropertyDescriptor destinationProperty, Object destination) throws MappingException;

  /**
   * Performs a single value transformation. This method is used to provide single field mappings via
   * {@link MappingModel}, therefore this method should work without side-effects like accessing fields to read or write
   * mapping values.
   *
   * @param source The source object to map from.
   * @param destination The destination object to map to.
   * @return Returns a {@link MappedResult} specifying the mapping value or signals to skip the mapping, because the
   *         transformation does not produce a destination value.
   * @throws MappingException Thrown on any mapping exception.
   */
  protected abstract MappedResult performValueTransformation(@Nullable Object source, @Nullable Object destination)
      throws MappingException;

  /**
   * Lets this transformation validate its configuration. If the state of this transformation is invalid,
   * implementations may throw a {@link MappingException}.
   *
   * @throws MappingException Thrown if the transformation setup is invalid
   */
  protected abstract void validateTransformation() throws MappingException;

  /**
   * Returns a mapper to map the specified source type to the specified destination type when registered.
   *
   * @param sourceType The source type
   * @param destinationType The destination type
   * @return Returns a mapper for the specified mapping if one was registered. Otherwise a {@link MappingException} is
   *         thrown.
   */
  <S, T> InternalMapper<S, T> getMapperFor(Class<S> sourceType, Class<T> destinationType) {
    return this.mapping.getMapperFor(getSourceProperty(), sourceType, getDestinationProperty(), destinationType);
  }

  /**
   * Returns a mapper to map the specified source type to the specified destination type or <code>null</code> if no
   * mapper was registered. In contrast to a {@link #hasMapperFor(Class, Class)}/{@link #getMapperFor(Class, Class)}
   * combination this requires only a single registry lookup.
   *
   * @param sourceType The source type
   * @param destinationType The destination type
   * @return Returns the registered mapper or <code>null</code>.
   */
  <S, T> InternalMapper<S, T> getMapperForOrNull(Class<S> sourceType, Class<T> destinationType) {
    return this.mapping.getMapperOrNull(sourceType, destinationType);
  }

  /**
   * Checks if the specified mapper is registered.
   *
   * @param sourceType The source type
   * @param destinationType the destination type
   * @return Returns <code>true</code> if a mapper was registered for this type of conversion. Otherwise
   *         <code>false</code>
   *         is returned.
   */
  public <S, T> boolean hasMapperFor(Class<S> sourceType, Class<T> destinationType) {
    return this.mapping.hasMapperFor(sourceType, destinationType);
  }

  @Nullable
  PropertyDescriptor getSourceProperty() {
    return sourceProperty;
  }

  @Nullable
  PropertyDescriptor getDestinationProperty() {
    return destinationProperty;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((destinationProperty == null) ? 0 : destinationProperty.hashCode());
    result = prime * result + ((sourceProperty == null) ? 0 : sourceProperty.hashCode());
    return result;
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Transformation other = (Transformation) obj;
    if (destinationProperty == null) {
      if (other.destinationProperty != null) {
        return false;
      }
    } else if (!destinationProperty.equals(other.destinationProperty)) {
      return false;
    }
    if (sourceProperty == null) {
      if (other.sourceProperty != null) {
        return false;
      }
    } else if (!sourceProperty.equals(other.sourceProperty)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return toString(false);
  }

  public abstract String toString(boolean detailed);

}
