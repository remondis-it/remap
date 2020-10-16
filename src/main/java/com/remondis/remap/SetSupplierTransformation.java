package com.remondis.remap;

import com.googlecode.openbeans.PropertyDescriptor;

import java.util.function.Supplier;

/**
 * A set transformation set a value supplied by a custom function to a destination field.
 *
 * @param <RS> The input type
 * @param <RD> The output type
 * @author schuettec
 */
class SetSupplierTransformation<S, D, RD> extends SetTransformation<S, D, RD> {

  SetSupplierTransformation(MappingConfiguration<S, D> mapping, PropertyDescriptor destProperty,
      Supplier<RD> supplier) {
    super(mapping, destProperty, S -> supplier.get());
  }

}
