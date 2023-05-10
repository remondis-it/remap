package com.remondis.remap.utils.property.walker;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PropertyAccess<T, P> {

  private final T target;
  private final T source;
  private final GetSet<T, P> sourceGetSet;
  private final GetSet<T, P> targetGetSet;

  PropertyAccess(T source, T target, Function<T, P> propertyExtractorSource, Function<T, P> propertyExtractorTarget,
      BiConsumer<T, P> propertyWriterSource, BiConsumer<T, P> propertyWriterTarget) {
    this.source = source;
    this.target = target;
    this.sourceGetSet = new GetSet<>(source, propertyExtractorSource, propertyWriterSource);
    this.targetGetSet = new GetSet<>(target, propertyExtractorTarget, propertyWriterTarget);
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
