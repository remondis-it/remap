package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;
import static com.remondis.remap.Mapping.getTypedPropertyFromFieldSelector;

import java.beans.PropertyDescriptor;

/**
 * Builds a reassing operation.
 *
 * @param <S> The source type.
 * @param <D> The destination type.
 * @param <RS> The source field type.
 */
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
   * @param destinationSelector
   *        {@link TypedSelector} to select the destination field.
   *
   * @return Returns the {@link Mapping} for further mapping configuration.
   */
  public Mapping<S, D> to(TypedSelector<RS, D> destinationSelector) {
    denyNull("destinationSelector", destinationSelector);
    TypedPropertyDescriptor<RS> typedDestProperty = getTypedPropertyFromFieldSelector(Target.DESTINATION, ASSIGN,
        destination, destinationSelector);
    PropertyDescriptor sourceProperty = typedSourceProperty.property;
    PropertyDescriptor destinationProperty = typedDestProperty.property;
    ReassignTransformation transformation = new ReassignTransformation(mapping, sourceProperty, destinationProperty);
    mapping.addMapping(sourceProperty, destinationProperty, transformation);
    return mapping;
  }

}
