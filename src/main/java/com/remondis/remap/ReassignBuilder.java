package com.remondis.remap;

import static com.remondis.remap.Mapping.getTypedPropertyFromFieldSelector;

import java.beans.PropertyDescriptor;

public class ReassignBuilder<S, D, RS> {

  static final String ASSIGN = "assign";

  private TypedPropertyDescriptor<RS> typedSourceProperty;

  private Mapping<S, D> mapping;

  private Class<D> destination;

  ReassignBuilder(TypedPropertyDescriptor<RS> typedSourceProperty, Class<D> destination, Mapping<S, D> mapping) {
    super();
    this.typedSourceProperty = typedSourceProperty;
    this.mapping = mapping;
    this.destination = destination;
  }

  /**
   * Reassings a source field to the specified destination field.
   *
   * @param destinationSelector {@link TypedSelector} to select the destination field.
   * @return Returns the {@link Mapping} for further mapping configuration.
   */
  public Mapping<S, D> to(TypedSelector<RS, D> destinationSelector) {
    TypedPropertyDescriptor<RS> typedDestProperty = getTypedPropertyFromFieldSelector(ASSIGN, destination,
      destinationSelector);
    PropertyDescriptor sourceProperty = typedSourceProperty.property;
    PropertyDescriptor destinationProperty = typedDestProperty.property;
    ReassignTransformation transformation = new ReassignTransformation(mapping, sourceProperty, destinationProperty);
    mapping.addMapping(sourceProperty, destinationProperty, transformation);
    return mapping;
  }

}
