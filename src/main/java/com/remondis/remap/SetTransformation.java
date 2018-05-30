package com.remondis.remap;

import static com.remondis.remap.Properties.asString;

import java.beans.PropertyDescriptor;
import java.util.function.Function;

/**
 * A replace transformation converts a source object into a destination object by applying the specified {@link
 * Transform} function on the source.
 *
 * @param <RS> The input type
 * @param <RD> The output type
 * @author schuettec
 */
class SetTransformation<S, D, RD> extends Transformation {

  private static final String REPLACE_MSG = "Set %s\n           with a custom value supplier.";

  private Function<S, RD> transformation;

  SetTransformation(Mapping<S, D> mapping, PropertyDescriptor destProperty, Function<S, RD> transformation) {
    // TODO: The null argument can be a big problem.
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
  public String toString() {
    return String.format(REPLACE_MSG, asString(destinationProperty));
  }

}
