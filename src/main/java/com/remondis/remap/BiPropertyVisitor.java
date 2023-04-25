package com.remondis.remap;

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
  private BiConsumer<P, P> biConsumer;

  public BiPropertyVisitor(Class<T> bean, Function<T, P> propertyExtractor, BiConsumer<P, P> biConsumer) {
    this.bean = bean;
    this.propertyExtractor = propertyExtractor;
    this.biConsumer = biConsumer;
  }

  protected void execute(T source, T target) {
    P sourcePropertyValue = propertyExtractor.apply(source);
    P targetPropertyValue = propertyExtractor.apply(target);
    biConsumer.accept(sourcePropertyValue, targetPropertyValue);
  }

  BiConsumer<P, P> getBiConsumer() {
    return biConsumer;
  }

  Class<T> getBean() {
    return bean;
  }

  Function<T, P> getPropertyExtractor() {
    return propertyExtractor;
  }

}
