package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.match.MatchMethod;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class MapOverCollectionMatchBuilder<M extends MapOverCommon<R, T>, R, T, TT> {
  protected final M mapOver;
  protected final Function<T, Collection<TT>> propertyExtractor;

  public MapOverCollectionMatchBuilder(M mapOver, Function<T, Collection<TT>> propertyExtractor) {
    this.mapOver = mapOver;
    this.propertyExtractor = propertyExtractor;
  }

  public MapOverCollectionBuilder<M, R, T, TT> matchedByProperty(Function<TT, Object> matchProperty) {
    return new MapOverCollectionBuilder<>(mapOver, propertyExtractor, new MatchMethod<>(matchProperty));
  }

  public MapOverCollectionBuilder<M, R, T, TT> matchedByFunction(BiPredicate<TT, TT> matchFunction) {
    return new MapOverCollectionBuilder<>(mapOver, propertyExtractor, new MatchMethod<>(matchFunction));
  }
}
