package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;
import static com.remondis.remap.MappingBuilder.getPropertyFromFieldSelector;

import java.beans.PropertyDescriptor;

/**
 * Builds a reassing operation.
 *
 * @param <S> The source type.
 * @param <D> The destination type.
 */
public class ReassignBuilder<S, D> {

  static final String ASSIGN = "assign";

  private PropertyDescriptor sourceProperty;

  private MappingBuilder<S, D> mapping;

  private Class<D> destination;

  ReassignBuilder(PropertyDescriptor sourceProperty, Class<D> destination, MappingBuilder<S, D> mapping) {
    super();
    this.sourceProperty = sourceProperty;
    this.mapping = mapping;
    this.destination = destination;
  }

  /**
   * Reassings a source field to the specified destination field.
   *
   * @param destinationSelector
   *        {@link TypedSelector} to select the destination field.
   *
   * @return Returns the {@link MappingBuilder} for further mapping configuration.
   */
  public MappingBuilder<S, D> to(FieldSelector<D> destinationSelector) {
    denyNull("destinationSelector", destinationSelector);
    PropertyDescriptor destProperty = getPropertyFromFieldSelector(Target.DESTINATION, ASSIGN, destination,
        destinationSelector);
    ReassignTransformation transformation = new ReassignTransformation(mapping, sourceProperty, destProperty);
    mapping.addMapping(sourceProperty, destProperty, transformation);
    return mapping;
  }

}
