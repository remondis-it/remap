package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.walker.BiRecursivePropertyWalker;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface MapOverStructure<R, T> extends MapOverBase<R, T> {

  @SuppressWarnings("unchecked")
  default <TT> MapOver<R, TT> goInto(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter,
      Class<TT> beanType) {
    BiRecursivePropertyWalker<T, TT> goIntoWalker = getWalker().goInto(propertyExtractor, propertyWriter, beanType);
    return new MapOver<>(getRoot(), (BiRecursivePropertyWalker<TT, TT>) goIntoWalker);
  }

  default MapOver<R, R> root() {
    return getRoot();
  }

  default MapOver<R, R> build() {
    return getRoot();
  }
}
