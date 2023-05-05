package com.remondis.remap.utils.mapover;

import static java.util.Objects.requireNonNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.remondis.remap.utils.property.walker.BiRecursivePropertyWalker;

import lombok.Getter;

@Getter
public class MapOverBase<R, T> extends MapOverCommon<R, T> {

  private final MapOverBase<R, R> root;

  @SuppressWarnings("unchecked")
  protected MapOverBase(Class<T> beanType) {
    super(beanType);
    this.root = (MapOverBase<R, R>) this;
  }

  protected MapOverBase(MapOverBase<R, R> root, BiRecursivePropertyWalker<T, T> walker) {
    super(walker);
    requireNonNull(root, "root must not be null!");
    this.root = root;
  }

  public <TT> MapOverPropertyBuilder<MapOverBase<R, T>, R, T, TT> mapProperty(
      Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter) {
    return new MapOverPropertyBuilder<>(this, propertyExtractor, propertyWriter);
  }

  @SuppressWarnings("unchecked")
  public <TT> MapOverBase<R, TT> goInto(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter,
      Class<TT> beanType) {
    BiRecursivePropertyWalker<T, TT> goIntoWalker = getWalker().goInto(propertyExtractor, propertyWriter, beanType);
    return new MapOverBase<>(getRoot(), (BiRecursivePropertyWalker<TT, TT>) goIntoWalker);
  }

  public MapOverBase<R, R> root() {
    return getRoot();
  }

  public MapOver<R, R> build() {
    return getRoot();
  }

}
