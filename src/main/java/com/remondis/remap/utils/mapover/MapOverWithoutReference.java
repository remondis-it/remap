package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.visitor.MapOverReferenceVisitor;
import com.remondis.remap.utils.property.walker.BiRecursivePropertyWalker;

import lombok.Getter;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@Getter
public class MapOverWithoutReference<R, T> extends MapOverImpl<R, T> {

  private final MapOverWithoutReference<R, R> root;

  @SuppressWarnings("unchecked")
  protected MapOverWithoutReference(Class<T> beanType) {
    super(beanType);
    this.root = (MapOverWithoutReference<R, R>) this;
  }

  protected MapOverWithoutReference(MapOverWithoutReference<R, R> root, BiRecursivePropertyWalker<T, T> walker) {
    super(walker);
    requireNonNull(root, "root must not be null!");
    this.root = root;
  }

  public <TT, ID> MapOverPropertyWithoutReferenceBuilder<MapOverWithoutReference<R, T>, R, T, TT> mapProperty(
      Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter) {
    return new MapOverPropertyWithoutReferenceBuilder<>(this, propertyExtractor, propertyWriter);
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

  public MapOver<R, R> build() {
    return getRoot();
  }

}
