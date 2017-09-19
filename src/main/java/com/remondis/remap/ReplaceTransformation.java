package com.remondis.remap;

import static com.remondis.remap.Properties.asString;

import java.beans.PropertyDescriptor;

/**
 * A replace transformation converts a source object into a destination object by applying the specified
 * {@link Transform} function on the source.
 *
 * @param <RS>
 *          The input type
 * @param <RD>
 *          The output type
 *
 * @author schuettec
 */
class ReplaceTransformation<RD, RS> extends Transformation {

  private static final String REPLACE_MSG = "Replacing %s\n           with %s using transformation";
  private static final String REPLACE_SKIPPED_MSG = "Replacing but skipping when null %s\n"
      + "           with %s using transformation";

  private Transform<RD, RS> transformation;
  private boolean skipWhenNull;

  ReplaceTransformation(Mapping<?, ?> mapping, PropertyDescriptor sourceProperty, PropertyDescriptor destProperty,
      Transform<RD, RS> transformation, boolean skipWhenNull) {
    super(mapping, sourceProperty, destProperty);
    this.transformation = transformation;
    this.skipWhenNull = skipWhenNull;
  }

  @Override
  protected void performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty, Object destination) throws MappingException {
    Object sourceValue = readOrFail(sourceProperty, source);

    if (sourceValue == null && skipWhenNull) {
      // Skip if source value is null and the transformation was declared to skip on null input.
      return;
    }

    @SuppressWarnings("unchecked")
    RD destinationValue = transformation.transform((RS) sourceValue);
    writeOrFail(destinationProperty, destination, destinationValue);
  }

  @Override
  protected void validateTransformation() throws MappingException {
  }

  @Override
  public String toString() {
    if (skipWhenNull) {
      return String.format(REPLACE_SKIPPED_MSG, asString(sourceProperty), asString(destinationProperty));
    } else {
      return String.format(REPLACE_MSG, asString(sourceProperty), asString(destinationProperty));
    }
  }

  Transform<RD, RS> getTransformation() {
    return transformation;
  }

  boolean isSkipWhenNull() {
    return skipWhenNull;
  }

}
