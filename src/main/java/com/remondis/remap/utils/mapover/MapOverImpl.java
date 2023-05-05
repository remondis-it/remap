package com.remondis.remap.utils.mapover;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PROTECTED;

import com.remondis.remap.utils.property.walker.BiRecursivePropertyWalker;

import lombok.Getter;

@Getter(PROTECTED)
public class MapOverImpl<R, T> implements MapOver<R, T> {

  private final BiRecursivePropertyWalker<T, T> walker;

  public MapOverImpl(BiRecursivePropertyWalker<T, T> walker) {
    requireNonNull(walker, "walker must not be null!");
    this.walker = walker;
  }

  public MapOverImpl(Class<T> beanType) {
    requireNonNull(beanType, "beanType must not be null!");
    this.walker = BiRecursivePropertyWalker.create(beanType);
  }

  @Override
  public void mapOver(T source, T target) {
    walker.execute(source, target);
  }
}
