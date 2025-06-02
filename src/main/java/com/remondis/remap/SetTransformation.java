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

  SetTransformation(MappingConfiguration<S, D> mapping, PropertyDescriptor destProperty,
      Function<S, RD> transformation) {
    super(mapping, null, destProperty);
    this.transformation = transformation;
  }

  @Override
  protected MappedResult performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty) throws MappingException {
    MappedResult result = performValueTransformation(source);
    return result;
    // if (result.hasValue()) {
    // writeOrFail(destinationProperty, destination, result.getValue());
    // }
  }

  @Override
  @SuppressWarnings("unchecked")
  protected MappedResult performValueTransformation(Object source) throws MappingException {
    Object destinationValue = transformation.apply((S) source);
    return MappedResult.value(destinationValue);
  }

  @Override
  protected void validateTransformation() throws MappingException {
  }

  @Override
  public String toString(boolean detailed) {
    return String.format(MSG, asString(destinationProperty, detailed));
  }
}
