package com.remondis.remap.utils.propertywalker;

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * @param <T> The bean type.
 * @param <P> The property type.
 *
 */
public class BiPropertyVisitor<T, P> {

  private Class<T> bean;
  private Function<T, P> propertyExtractor;
  private BiConsumer<T, P> propertyWriter;

  private VisitorFunction<T, P> visitorFunction;

  public BiPropertyVisitor(Class<T> bean, Function<T, P> propertyExtractor, BiConsumer<T, P> propertyWriter,
      VisitorFunction<T, P> visitorFunction) {
    this.bean = bean;
    this.propertyExtractor = propertyExtractor;
    this.propertyWriter = propertyWriter;
    this.visitorFunction = visitorFunction;
  }

  protected void execute(T source, T target) {
    PropertyAccess<T, P> propertyAccess = new PropertyAccess<T, P>(source, target, propertyExtractor, propertyWriter);
    visitorFunction.consume(propertyAccess);
  }

}
