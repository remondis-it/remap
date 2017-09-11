package com.remondis.remap;

import static com.remondis.remap.Lang.*;

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

  @SuppressWarnings("rawtypes")
  private Transform transformation;
  private boolean skipWhenNull;

  ReplaceTransformation(Mapping<?, ?> mapping, PropertyDescriptor sourceProperty, PropertyDescriptor destProperty,
      Transform<RD, RS> transformation, boolean skipWhenNull) {
    super(mapping, sourceProperty, destProperty);
    denyNull("transformation", transformation);
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
    Object destinationValue = transformation.transform(sourceValue);
    writeOrFail(destinationProperty, destination, destinationValue);
  }

  @Override
  protected void validateTransformation() throws MappingException {
  }

}
