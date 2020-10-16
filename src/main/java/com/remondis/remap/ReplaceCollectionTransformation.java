package com.remondis.remap;

import com.googlecode.openbeans.PropertyDescriptor;

import static com.remondis.remap.Properties.asString;
import static com.remondis.remap.ReflectionUtil.getCollector;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collector;

import javax.xml.crypto.dsig.Transform;

/**
 * A replace transformation converts a source object into a destination object by applying the specified {@link
 * Transform} function on the source.
 *
 * @param <RS> The input type
 * @param <RD> The output type
 * @author schuettec
 */
class ReplaceCollectionTransformation<RS, RD> extends SkipWhenNullTransformation<RS, RD> {

  private static final String REPLACE_MSG = "Replacing %s\n           with %s using transformation";
  private static final String REPLACE_SKIPPED_MSG = "Replacing but skipping when null %s\n"
      + "           with %s using transformation";

  private Function<RS, RD> transformation;
  private boolean skipWhenNull;

  ReplaceCollectionTransformation(MappingConfiguration<?, ?> mapping, PropertyDescriptor sourceProperty,
      PropertyDescriptor destProperty, Function<RS, RD> transformation, boolean skipWhenNull) {
    super(mapping, sourceProperty, destProperty);
    this.transformation = transformation;
    this.skipWhenNull = skipWhenNull;
  }

  @Override
  protected void performTransformation(PropertyDescriptor sourceProperty, Object source,
                                       PropertyDescriptor destinationProperty, Object destination) throws MappingException {
    Object sourceValue = readOrFail(sourceProperty, source);

    MappedResult result = performValueTransformation(sourceValue, destination);

    if (result.hasValue()) {
      writeOrFail(destinationProperty, destination, result.getValue());
    }
  }

  @SuppressWarnings({
      "rawtypes", "unchecked"
  })
  @Override
  protected MappedResult performValueTransformation(Object source, Object destination) throws MappingException {
    if (source == null) {
      // Skip if source value is null and the transformation was declared to skip on null input.
      return MappedResult.skip();
    } else {
      Collection collection = (Collection) source;

      Class<?> destinationCollectionType = destinationProperty.getPropertyType();
      Collector collector = getCollector(destinationCollectionType);
      Collection<RD> destinationValue = null;

      // Skip when null on collection means to skip null items.
      if (skipWhenNull) {
        destinationValue = (Collection<RD>) collection.stream()
            .filter(i -> (i != null))
            .map(sourceItem -> transformation.apply((RS) sourceItem))
            .collect(collector);
      } else {
        destinationValue = (Collection<RD>) collection.stream()
            .map(sourceItem -> {
              RS sourceItem2 = null;
              sourceItem2 = (RS) sourceItem;
              return transformation.apply(sourceItem2);
            })
            .collect(collector);
      }
      return MappedResult.value(destinationValue);
    }
  }

  @Override
  protected void validateTransformation() throws MappingException {
  }

  @Override
  Function<RS, RD> getTransformation() {
    return transformation;
  }

  @Override
  boolean isSkipWhenNull() {
    return skipWhenNull;
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

}
