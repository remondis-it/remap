package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.walker.BiRecursivePropertyWalker;

public interface MapOverBase<R, T> {
  MapOver<R, R> getRoot();

  BiRecursivePropertyWalker<T, T> getWalker();
}
