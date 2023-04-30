package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.walker.BiRecursivePropertyWalker;

import lombok.Getter;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@Getter
public class MapOverWithoutReference<R, T> extends MapOver<R, T>
    implements MapOverPropertyWithoutReference<MapOverWithoutReference<R, T>, R, T>,
    MapOverCollectionByPropertyWithoutReference<MapOverWithoutReference<R, T>, R, T>,
    MapOverCollectionByFunctionWithoutReference<MapOverWithoutReference<R, T>, R, T>,
    MapOverMapWithoutReference<MapOverWithoutReference<R, T>, R, T> {

  private final MapOverWithoutReference<R, R> root;
  private final BiRecursivePropertyWalker<T, T> walker;

  @SuppressWarnings("unchecked")
  protected MapOverWithoutReference(Class<T> beanType) {
    requireNonNull(beanType, "beanType must not be null!");
    this.root = (MapOverWithoutReference<R, R>) this;
    this.walker = BiRecursivePropertyWalker.create(beanType);
  }

  protected MapOverWithoutReference(MapOverWithoutReference<R, R> root, BiRecursivePropertyWalker<T, T> walker) {
    requireNonNull(root, "root must not be null!");
    this.root = root;
    this.walker = walker;
  }

  @SuppressWarnings("unchecked")
  public <TT> MapOverWithoutReference<R, TT> goInto(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter,
      Class<TT> beanType) {
    BiRecursivePropertyWalker<T, TT> goIntoWalker = getWalker().goInto(propertyExtractor, propertyWriter, beanType);
    return new MapOverWithoutReference<>(getRoot(), (BiRecursivePropertyWalker<TT, TT>) goIntoWalker);
  }

  public MapOverWithoutReference<R, R> root() {
    return getRoot();
  }
}
