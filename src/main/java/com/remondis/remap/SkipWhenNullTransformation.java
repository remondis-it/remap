package com.remondis.remap;

import java.beans.PropertyDescriptor;

/**
 * Interface for transformations that can be skippen on <code>null</code> input.
 */
abstract class SkipWhenNullTransformation<RS, RD> extends Transformation {

  SkipWhenNullTransformation(Mapping<?, ?> mapping, PropertyDescriptor sourceProperty,
      PropertyDescriptor destinationProperty) {
    super(mapping, sourceProperty, destinationProperty);
  }

  /**
   * @return Returns <code>true</code> if this transformation should be skipped when the input value is
   *         <code>null</code>, otherwise <code>false</code> is returned.
   */
  abstract boolean isSkipWhenNull();

  abstract Transform<RS, RD> getTransformation();
}
