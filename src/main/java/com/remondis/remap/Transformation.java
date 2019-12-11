package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This is the base class for a transformation that performs a single step when mapping from an object to another
 * object. There will be different implementations for mapping operations.
 *
 * @author schuettec
 */
abstract class Transformation {

  protected PropertyDescriptor sourceProperty;
  protected PropertyDescriptor destinationProperty;
  private MappingConfiguration<?, ?> mapping;

  Transformation(MappingConfiguration<?, ?> mapping, PropertyDescriptor sourceProperty, PropertyDescriptor destinationProperty) {
    super();
    denyNull("mapping", mapping);
    this.mapping = mapping;
    this.sourceProperty = sourceProperty;
    this.destinationProperty = destinationProperty;
  }

  protected Class<?> getDestinationType() {
    return destinationProperty.getPropertyType();
  }

  protected Class<?> getSourceType() {
    return sourceProperty.getPropertyType();
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
  protected abstract void performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty, Object destination) throws MappingException;

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
  <S, T> InternalMapper<S, T> getMapperFor(PropertyDescriptor sourceProperty, Class<S> sourceType,
      PropertyDescriptor destinationProperty, Class<T> destinationType) {
    return this.mapping.getMapperFor(sourceProperty, sourceType, destinationProperty, destinationType);
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

  PropertyDescriptor getSourceProperty() {
    return sourceProperty;
  }

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
  public boolean equals(Object obj) {
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
