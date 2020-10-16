package com.remondis.remap;

import com.googlecode.openbeans.PropertyDescriptor;

import static com.remondis.remap.Lang.denyNull;
import static com.remondis.remap.MappingConfiguration.getPropertyFromFieldSelector;


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

  private PropertyDescriptor sourceProperty;

  private AssertConfiguration<S, D> asserts;

  private Class<D> destination;

  ReassignAssertBuilder(PropertyDescriptor sourceProperty, Class<D> destination, AssertConfiguration<S, D> asserts) {
    super();
    this.sourceProperty = sourceProperty;
    this.asserts = asserts;
    this.destination = destination;
  }

  /**
   * Reassigns a source field to the specified destination field.
   *
   * @param destinationSelector
   *        {@link TypedSelector} to select the destination field.
   *
   * @return Returns the {@link MappingConfiguration} for further mapping configuration.
   */
  public AssertConfiguration<S, D> to(FieldSelector<D> destinationSelector) {
    denyNull("destinationSelector", destinationSelector);
    PropertyDescriptor destinationProperty = getPropertyFromFieldSelector(Target.DESTINATION, ReassignBuilder.ASSIGN,
        this.destination, destinationSelector);
    ReassignTransformation transformation = new ReassignTransformation(asserts.getMapping(), sourceProperty,
        destinationProperty);
    asserts.addAssertion(transformation);
    return asserts;
  }

}
