package com.remondis.remap;

import static com.remondis.remap.Properties.asString;

import java.beans.PropertyDescriptor;
import java.util.function.Function;

import javax.xml.crypto.dsig.Transform;

/**
 * A replace transformation converts a source object into a destination object by applying the specified {@link
 * Transform} function on the source.
 *
 * @param <RS> The input type
 * @param <RD> The output type
 * @author schuettec
 */
class SetTransformation<S, D, RD> extends Transformation {

  private static final String MSG = "Set %s with a custom value supplier.";

  private Function<S, RD> transformation;

  SetTransformation(MappingConfiguration<S, D> mapping, PropertyDescriptor destProperty, Function<S, RD> transformation) {
    super(mapping, null, destProperty);
    this.transformation = transformation;
  }

  @Override
  @SuppressWarnings({
      "unchecked"
  })
  protected void performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty, Object destination) throws MappingException {
    RD destinationValue = transformation.apply((S) source);
    writeOrFail(destinationProperty, destination, destinationValue);
  }

  @Override
  protected void validateTransformation() throws MappingException {
  }

  @Override
  public String toString(boolean detailed) {
    return String.format(MSG, asString(destinationProperty, detailed));
  }
}
