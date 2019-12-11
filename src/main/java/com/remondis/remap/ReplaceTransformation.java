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
class ReplaceTransformation<RS, RD> extends SkipWhenNullTransformation<RS, RD> {

  private static final String REPLACE_MSG = "Replacing %s\n           with %s using transformation";
  private static final String REPLACE_SKIPPED_MSG = "Replacing but skipping when null %s\n"
      + "           with %s using transformation";

  private Function<RS, RD> transformation;
  private boolean skipWhenNull;

  ReplaceTransformation(MappingConfiguration<?, ?> mapping, PropertyDescriptor sourceProperty, PropertyDescriptor destProperty,
      Function<RS, RD> transformation, boolean skipWhenNull) {
    super(mapping, sourceProperty, destProperty);
    this.transformation = transformation;
    this.skipWhenNull = skipWhenNull;
  }

  @Override
  @SuppressWarnings({
      "unchecked"
  })
  protected void performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty, Object destination) throws MappingException {
    Object sourceValue = readOrFail(sourceProperty, source);

    if (sourceValue == null && skipWhenNull) {
      // Skip if source value is null and the transformation was declared to skip on null input.
      return;
    }
    RD destinationValue = transformation.apply((RS) sourceValue);
    writeOrFail(destinationProperty, destination, destinationValue);
  }

  @Override
  protected void validateTransformation() throws MappingException {
  }

  @Override
  public String toString(boolean detailed) {
    if (skipWhenNull) {
      return String.format(REPLACE_SKIPPED_MSG, asString(sourceProperty, detailed),
          asString(destinationProperty, detailed));
    } else {
      return String.format(REPLACE_MSG, asString(sourceProperty, detailed), asString(destinationProperty, detailed));
    }
  }

  @Override
  Function<RS, RD> getTransformation() {
    return transformation;
  }

  @Override
  boolean isSkipWhenNull() {
    return skipWhenNull;
  }

}
