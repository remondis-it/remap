package com.remondis.remap;

import static com.remondis.remap.Properties.asString;
import static com.remondis.remap.ReassignTransformation.isCollection;
import static com.remondis.remap.ReflectionUtil.getCollector;

import java.beans.PropertyDescriptor;
import java.util.Collection;

/**
 * A replace transformation converts a source object into a destination object by applying the specified {@link
 * Transform} function on the source.
 *
 * @param <RS> The input type
 * @param <RD> The output type
 * @author schuettec
 */
class ReplaceTransformation<RS, RD> extends Transformation {

  private static final String REPLACE_MSG = "Replacing %s\n           with %s using transformation";
  private static final String REPLACE_SKIPPED_MSG = "Replacing but skipping when null %s\n"
      + "           with %s using transformation";

  private Transform<RS, RD> transformation;
  private boolean skipWhenNull;

  ReplaceTransformation(Mapping<?, ?> mapping, PropertyDescriptor sourceProperty, PropertyDescriptor destProperty,
      Transform<RS, RD> transformation, boolean skipWhenNull) {
    super(mapping, sourceProperty, destProperty);
    this.transformation = transformation;
    this.skipWhenNull = skipWhenNull;
  }

  @Override
  @SuppressWarnings({
      "rawtypes", "unchecked"
  })
  protected void performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty, Object destination) throws MappingException {
    Object sourceValue = readOrFail(sourceProperty, source);

    if (isCollection(sourceProperty.getPropertyType())) {
      if (sourceValue == null) {
        // Skip if source value is null and the transformation was declared to skip on null input.
        return;
      } else {
        Collection collection = (Collection) sourceValue;
        Collection<RD> destinationValue = null;

        // Skip when null on collection means to skip null items.
        if (skipWhenNull) {
          destinationValue = (Collection<RD>) collection.stream()
              .filter(i -> (i != null))
              .map(sourceItem -> transformation.transform((RS) sourceItem))
              .collect(getCollector(collection));
        } else {
          destinationValue = (Collection<RD>) collection.stream()
              .map(sourceItem -> transformation.transform((RS) sourceItem))
              .collect(getCollector(collection));
        }
        writeOrFail(destinationProperty, destination, destinationValue);
      }
    } else {
      if (sourceValue == null && skipWhenNull) {
        // Skip if source value is null and the transformation was declared to skip on null input.
        return;
      }
      RD destinationValue = transformation.transform((RS) sourceValue);
      writeOrFail(destinationProperty, destination, destinationValue);
    }
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

  Transform<RS, RD> getTransformation() {
    return transformation;
  }

  boolean isSkipWhenNull() {
    return skipWhenNull;
  }

}
