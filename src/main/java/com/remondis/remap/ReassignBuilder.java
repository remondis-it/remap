package com.remondis.remap;

import java.beans.PropertyDescriptor;

public class ReassignBuilder<S, D, RS> {

  private static final String ASSIGN = "assign";

  TypedPropertyDescriptor<RS> tSourceProperty;

  Mapping<S, D> mapping;

  Class<D> destination;

  /**
   * Reassings a source field to the specified destination field.
   * 
   * @param destinationSelector
   *          {@link TypedSelector} to select the destination field.
   *
   * @return Returns the {@link Mapping} for further mapping configuration.
   */
  public Mapping<S, D> to(TypedSelector<RS, D> destinationSelector) {
    TypedPropertyDescriptor<RS> typedDestProperty = mapping.getTypedPropertyFromFieldSelector(ASSIGN, this.destination,
        destinationSelector);
    PropertyDescriptor sourceProperty = tSourceProperty.property;
    PropertyDescriptor destinationProperty = typedDestProperty.property;
    ReassignTransformation transformation = new ReassignTransformation(mapping, sourceProperty, destinationProperty);
    mapping.addMapping(sourceProperty, destinationProperty, transformation);
    return mapping;
  }

}
