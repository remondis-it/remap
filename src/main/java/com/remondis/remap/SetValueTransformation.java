package com.remondis.remap;

import java.beans.PropertyDescriptor;

/**
 * A set transformation set a value supplied by a custom function to a destination field.
 *
 * @param <RS> The input type
 * @param <RD> The output type
 * @author schuettec
 */
class SetValueTransformation<S, D, RD> extends SetTransformation<S, D, RD> {

  SetValueTransformation(Mapping<S, D> mapping, PropertyDescriptor destProperty, RD value) {
    super(mapping, destProperty, S -> value);
  }

}
