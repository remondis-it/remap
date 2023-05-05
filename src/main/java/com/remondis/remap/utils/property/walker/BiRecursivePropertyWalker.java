package com.remondis.remap.utils.property.walker;

import com.remondis.remap.utils.property.visitor.VisitorFunction;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

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

  private final BiRecursivePropertyWalker<R, ?> rootWalker;
  private final Function<R, T> fromRootExctractor;

  private final Class<T> beanType;
  private final List<BiPropertyVisitor<T, ?>> visitors;

  @SuppressWarnings("unchecked")
  private BiRecursivePropertyWalker(Class<T> beanType) {
    this.beanType = beanType;
    this.rootWalker = this;
    this.fromRootExctractor = (Function<R, T>) identity();
    this.visitors = new LinkedList<>();
  }

  private <TT> BiRecursivePropertyWalker(BiRecursivePropertyWalker<R, ?> rootWalker, Function<R, TT> fromRootExctractor,
      Function<TT, T> propertyExtractor, Class<T> beanType) {
    this.rootWalker = rootWalker;
    this.fromRootExctractor = fromRootExctractor.andThen(propertyExtractor);
    this.beanType = beanType;
    this.visitors = new LinkedList<>();
  }

  public <TT> BiRecursivePropertyWalker<R, TT> goInto(Function<T, TT> propertyExtractor,
      BiConsumer<T, TT> propertyWriter, Class<TT> beanType) {
    requireNonNull(propertyExtractor, "propertyExtractor may not be null!");
    requireNonNull(beanType, "beanType may not be null!");
    BiRecursivePropertyWalker<R, TT> recursivePropertyWalker = new BiRecursivePropertyWalker<>(rootWalker,
        fromRootExctractor, propertyExtractor, beanType);

    addProperty(propertyExtractor, propertyWriter, access -> recursivePropertyWalker.execute(access.sourceProperty()
        .get(),
        access.targetProperty()
            .get()));

    return recursivePropertyWalker;
  }

  public <P> BiRecursivePropertyWalker<R, T> addProperty(Function<T, P> propertyExtractor,
      BiConsumer<T, P> propertyWriter, VisitorFunction<T, P> biConsumer) {
    requireNonNull(propertyExtractor, "propertyExtractor may not be null!");
    BiPropertyVisitor<T, P> pv = new BiPropertyVisitor<>(beanType, propertyExtractor, propertyWriter, biConsumer);
    visitors.add(pv);
    return this;
  }

  public <P> BiRecursivePropertyWalker<R, T> addProperty(Function<T, P> propertyExtractor,
      VisitorFunction<T, P> biConsumer) {
    requireNonNull(propertyExtractor, "propertyExtractor may not be null!");
    BiPropertyVisitor<T, P> pv = new BiPropertyVisitor<>(beanType, propertyExtractor, biConsumer);
    visitors.add(pv);
    return this;
  }

  public <P> BiRecursivePropertyWalker<R, T> addProperty(Function<T, P> propertyExtractorSource,
      Function<T, P> propertyExtractorTarget, VisitorFunction<T, P> biConsumer) {
    requireNonNull(propertyExtractorSource, "propertyExtractorSource may not be null!");
    requireNonNull(propertyExtractorTarget, "propertyExtractorTarget may not be null!");
    BiPropertyVisitor<T, P> pv = new BiPropertyVisitor<>(beanType, propertyExtractorSource, propertyExtractorTarget,
        biConsumer);
    visitors.add(pv);
    return this;
  }

  public <P> BiRecursivePropertyWalker<R, T> addProperty(Function<T, P> propertyExtractorSource,
      Function<T, P> propertyExtractorTarget, BiConsumer<T, P> propertyWriterSource,
      BiConsumer<T, P> propertyWriterTarget, VisitorFunction<T, P> biConsumer) {
    requireNonNull(propertyExtractorSource, "propertyExtractorSource may not be null!");
    requireNonNull(propertyExtractorTarget, "propertyExtractorTarget may not be null!");
    BiPropertyVisitor<T, P> pv = new BiPropertyVisitor<>(beanType, propertyExtractorSource, propertyExtractorTarget,
        propertyWriterSource, propertyWriterTarget, biConsumer);
    visitors.add(pv);
    return this;
  }

  public void execute(T source, T target) {
    visitors.parallelStream()
        .forEach(visitor -> visitor.execute(source, target));
  }

  public static <T> BiRecursivePropertyWalker<T, T> create(Class<T> beanType) {
    return new BiRecursivePropertyWalker<>(beanType);
  }

  @SuppressWarnings("unchecked")
  public BiRecursivePropertyWalker<R, R> build() {
    return (BiRecursivePropertyWalker<R, R>) rootWalker;
  }
}
