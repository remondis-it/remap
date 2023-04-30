package com.remondis.remap.utils.property.walker;

import lombok.RequiredArgsConstructor;

import java.util.function.BiConsumer;
import java.util.function.Function;

@RequiredArgsConstructor
public class GetSet<T, P> {

  private final T object;
  private final Function<T, P> propertyExtractor;
  private final BiConsumer<T, P> propertyWriter;

  public void set(P value) {
    propertyWriter.accept(object, value);
  }

  public P get() {
    return propertyExtractor.apply(object);
  }
}
