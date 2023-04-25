package com.remondis.remap.utils.propertywalker;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PropertyAccess<T, P> {

  private T target;
  private T source;
  private GetSet<T, P> sourceGetSet;
  private GetSet<T, P> targetGetSet;

  PropertyAccess(T source, T target, Function<T, P> propertyExtractor, BiConsumer<T, P> propertyWriter) {
    super();
    this.source = source;
    this.target = target;
    this.sourceGetSet = GetSet.create(source, propertyExtractor, propertyWriter);
    this.targetGetSet = GetSet.create(source, propertyExtractor, propertyWriter);
  }

  public T source() {
    return source;
  }

  public T target() {
    return target;
  }

  public GetSet<T, P> sourceProperty() {
    return sourceGetSet;
  }

  public GetSet<T, P> targetProperty() {
    return targetGetSet;
  }
}
