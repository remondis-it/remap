package com.remondis.remap;

import static java.util.Objects.requireNonNull;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Class that provides a way to recursively tree-walk over specified properties.
 *
 * @param <R> The root bean type.
 * @param <T> The bean type.
 */
public class BiRecursivePropertyWalker<R, T> {

  BiRecursivePropertyWalker<R, ?> rootWalker;
  Function<R, T> fromRootExctractor;

  Class<T> beanType;
  List<BiPropertyVisitor<T, ?>> visitors;

  @SuppressWarnings("unchecked")
  private BiRecursivePropertyWalker(Class<T> beanType) {
    super();
    this.beanType = beanType;
    this.rootWalker = this;
    this.fromRootExctractor = (Function<R, T>) Function.identity();
    this.visitors = new LinkedList<>();
  }

  private <TT> BiRecursivePropertyWalker(BiRecursivePropertyWalker<R, ?> rootWalker, Function<R, TT> fromRootExctractor,
      Function<TT, T> propertyExtractor, Class<T> beanType) {
    super();
    this.rootWalker = this;
    this.fromRootExctractor = fromRootExctractor.andThen(propertyExtractor);
    this.beanType = beanType;
    this.visitors = new LinkedList<>();
  }

  public <TT> BiRecursivePropertyWalker<R, TT> goInto(Function<T, TT> propertyExtractor, Class<TT> beanType) {
    requireNonNull(propertyExtractor, "propertyExtractor may not be null!");
    requireNonNull(beanType, "beanType may not be null!");
    BiRecursivePropertyWalker<R, TT> recursivePropertyWalker = new BiRecursivePropertyWalker<>(rootWalker,
        fromRootExctractor, propertyExtractor, beanType);

    addProperty(propertyExtractor, new BiConsumer<TT, TT>() {

      @Override
      public void accept(TT source, TT target) {
        recursivePropertyWalker.execute(source, target);
      }
    });

    return recursivePropertyWalker;
  }

  public <P> BiRecursivePropertyWalker<R, T> addProperty(Function<T, P> propertyExtractor,
      BiConsumer<P, P> biConsumer) {
    requireNonNull(propertyExtractor, "propertyExtractor may not be null!");
    BiPropertyVisitor<T, P> pv = new BiPropertyVisitor<>(beanType, propertyExtractor, biConsumer);
    visitors.add(pv);
    return this;
  }

  @SuppressWarnings("unchecked")
  public void execute(T source, T target) {
    visitors.parallelStream()
        .forEach(visitor -> ((BiPropertyVisitor<T, T>) visitor).execute(source, target));
  }

  public static <T> BiRecursivePropertyWalker<T, T> create(Class<T> beanType) {
    return new BiRecursivePropertyWalker<>(beanType);
  }

  public BiRecursivePropertyWalker<R, R> build() {
    return (BiRecursivePropertyWalker<R, R>) rootWalker;
  }

}
