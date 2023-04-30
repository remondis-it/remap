package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.visitor.MapOverMatchPropertyCollectionVisitor;

import java.util.Collection;
import java.util.function.Function;

public interface MapOverCollectionByProperty<R, T> extends MapOverBase<R, T> {

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      Function<TT, Object> matchProperty) {
    return mapCollection(propertyExtractor, matchProperty, true, true);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchProperty, true, true);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      Function<TT, Object> matchProperty, boolean addNew, boolean orphanRemoval) {
    return mapCollection(propertyExtractor, propertyExtractor, matchProperty, addNew, orphanRemoval);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty, boolean addNew,
      boolean orphanRemoval) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchProperty, null, addNew, orphanRemoval);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      Function<TT, Object> matchProperty, MapOver<TT, TT> mapper) {
    return mapCollection(propertyExtractor, matchProperty, mapper, true, true);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty, MapOver<TT, TT> mapper) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchProperty, mapper, true, true);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      Function<TT, Object> matchProperty, MapOver<TT, TT> mapper, boolean addNew, boolean orphanRemoval) {
    return mapCollection(propertyExtractor, propertyExtractor, matchProperty, mapper, addNew, orphanRemoval);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty, MapOver<TT, TT> mapper,
      boolean addNew, boolean orphanRemoval) {
    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMatchPropertyCollectionVisitor<>(matchProperty, addNew, orphanRemoval, mapper));
    return (MapOver<R, T>) this;
  }
}
