package com.remondis.remap.utils.propertywalker;

public interface VisitorFunction<T, P> {
  public void consume(PropertyAccess<T, P> access);
}
