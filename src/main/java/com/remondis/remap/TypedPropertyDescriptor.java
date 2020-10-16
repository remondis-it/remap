package com.remondis.remap;


import com.googlecode.openbeans.PropertyDescriptor;

/**
 * A bucket to hold the generic type of a property and the {@link PropertyDescriptor}.
 *
 * @author schuettec
 */
class TypedPropertyDescriptor<R> {

  R returnValue;
  PropertyDescriptor property;

}
