package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;
import static com.remondis.remap.Mapping.getPropertyFromFieldSelector;

import java.beans.PropertyDescriptor;

/**
 * Builds a reassign operation.
 *
 * @param <S> The source type.
 * @param <D> The destination type.
 */
public class ReassignBuilder<S, D> {

  static final String ASSIGN = "assign";

  private PropertyDescriptor sourceProperty;

  private Mapping<S, D> mapping;

  private Class<D> destination;

  ReassignBuilder(PropertyDescriptor sourceProperty, Class<D> destination, Mapping<S, D> mapping) {
    super();
    this.sourceProperty = sourceProperty;
    this.mapping = mapping;
    this.destination = destination;
  }

  /**
   * Reassigns a source field to the specified destination field.
   *
   * @param destinationSelector
   *        {@link TypedSelector} to select the destination field.
   *
   * @return Returns the {@link Mapping} for further mapping configuration.
   */
  public Mapping<S, D> to(FieldSelector<D> destinationSelector) {
    denyNull("destinationSelector", destinationSelector);
    PropertyDescriptor destProperty = getPropertyFromFieldSelector(Target.DESTINATION, ASSIGN, destination,
        destinationSelector);
    ReassignTransformation transformation = new ReassignTransformation(mapping, sourceProperty, destProperty);
    mapping.addMapping(sourceProperty, destProperty, transformation);
    return mapping;
  }

}
