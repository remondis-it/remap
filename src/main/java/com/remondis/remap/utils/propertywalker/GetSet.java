package com.remondis.remap.utils.propertywalker;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class GetSet<T, P> {

  private T object;
  Function<T, P> propertyExtractor;
  BiConsumer<T, P> propertyWriter;

  private GetSet(T object, Function<T, P> propertyExtractor, BiConsumer<T, P> propertyWriter) {
    super();
    this.object = object;
    this.propertyExtractor = propertyExtractor;
    this.propertyWriter = propertyWriter;
  }

  public static <T, P> GetSet<T, P> create(T object, Function<T, P> propertyExtractor,
      BiConsumer<T, P> propertyWriter) {
    return new GetSet<>(object, propertyExtractor, propertyWriter);
  }

  public void set(P value) {
    propertyWriter.accept(object, value);
  }

  public P get() {
    return propertyExtractor.apply(object);
  }
}
