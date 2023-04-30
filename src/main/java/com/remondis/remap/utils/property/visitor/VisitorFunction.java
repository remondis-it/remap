package com.remondis.remap.utils.property.visitor;

import com.remondis.remap.utils.property.walker.PropertyAccess;

public interface VisitorFunction<T, P> {
  void consume(PropertyAccess<T, P> access);
}
