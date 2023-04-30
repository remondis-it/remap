package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.visitor.MapOverMatchFunctionCollectionVisitor;
import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Function;

public interface MapOverCollectionByFunction<R, T> extends MapOverBase<R, T> {
  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      BiPredicate<TT, TT> matchFunction) {
    return mapCollection(propertyExtractor, matchFunction, true, true);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchFunction, true, true);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      BiPredicate<TT, TT> matchFunction, boolean addNew, boolean orphanRemoval) {
    return mapCollection(propertyExtractor, propertyExtractor, matchFunction, addNew, orphanRemoval);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction, boolean addNew,
      boolean orphanRemoval) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchFunction, null, addNew, orphanRemoval);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      BiPredicate<TT, TT> matchFunction, MapOver<TT, TT> mapper) {
    return mapCollection(propertyExtractor, matchFunction, mapper, true, true);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction, MapOver<TT, TT> mapper) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchFunction, mapper, true, true);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      BiPredicate<TT, TT> matchFunction, MapOver<TT, TT> mapper, boolean addNew, boolean orphanRemoval) {
    return mapCollection(propertyExtractor, propertyExtractor, matchFunction, mapper, addNew, orphanRemoval);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction, MapOver<TT, TT> mapper,
      boolean addNew, boolean orphanRemoval) {
    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMatchFunctionCollectionVisitor<>(matchFunction, addNew, orphanRemoval, mapper));
    return (MapOver<R, T>) this;
  }
}
