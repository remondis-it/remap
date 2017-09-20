package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;
import static com.remondis.remap.Mapping.getTypedPropertyFromFieldSelector;

import java.beans.PropertyDescriptor;

/**
 * This class is used to build an assertion about a reassign operation.
 *
 * @param <S>
 *        The source object
 * @param <D>
 *        The destination object
 * @param <RS>
 *        The type of the selected field.
 *
 * @author schuettec
 */
public class ReassignAssertBuilder<S, D, RS> {

  private TypedPropertyDescriptor<RS> typedSourceProperty;

  private AssertMapping<S, D> asserts;

  private Class<D> destination;

  ReassignAssertBuilder(TypedPropertyDescriptor<RS> typedSourceProperty, Class<D> destination,
      AssertMapping<S, D> asserts) {
    super();
    this.typedSourceProperty = typedSourceProperty;
    this.asserts = asserts;
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
  public AssertMapping<S, D> to(TypedSelector<RS, D> destinationSelector) {
    denyNull("destinationSelector", destinationSelector);
  TypedPropertyDescriptor<RS> typedDestProperty = getTypedPropertyFromFieldSelector(ReassignBuilder.ASSIGN,
        this.destination, destinationSelector);
    PropertyDescriptor sourceProperty = typedSourceProperty.property;
    PropertyDescriptor destinationProperty = typedDestProperty.property;
    ReassignTransformation transformation = new ReassignTransformation(asserts.getMapping(), sourceProperty,
        destinationProperty);
    asserts.addAssertion(transformation);
    return asserts;
  }

}
