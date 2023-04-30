package com.remondis.remap.utils.property.visitor;

import com.remondis.remap.utils.property.walker.PropertyAccess;

public class MapOverOverrideVisitor<T, TT> implements VisitorFunction<T, TT> {

  @Override
  public void consume(PropertyAccess<T, TT> access) {
    TT sourceValue = access.sourceProperty()
        .get();
    access.targetProperty()
        .set(sourceValue);
  }
}
