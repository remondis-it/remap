package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.CollectionChangeFunction;
import com.remondis.remap.utils.property.visitor.MapOverMatchFunctionCollectionVisitor;
import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static com.remondis.remap.utils.property.ChangeType.ALL;

public interface MapOverCollectionByFunctionWithoutReference<M extends MapOver<R, T>, R, T> extends
    MapOverCommon<R, T> {

  // Override
  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractor, BiPredicate<TT, TT> matchFunction) {
    return mapCollection(propertyExtractor, matchFunction, ALL);
  }

  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchFunction, ALL);
  }

  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractor, BiPredicate<TT, TT> matchFunction,
      ChangeType changeType) {
    return mapCollection(propertyExtractor, propertyExtractor, matchFunction, changeType);
  }

  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction, ChangeType changeType) {

    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMatchFunctionCollectionVisitor<>(matchFunction, changeType, new CollectionChangeFunction<>()));
    return (M) this;
  }

  // Mapping
  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractor, BiPredicate<TT, TT> matchFunction,
      MapOver<TT, TT> mapper) {
    return mapCollection(propertyExtractor, matchFunction, mapper, ALL);
  }

  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction, MapOver<TT, TT> mapper) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchFunction, mapper, ALL);
  }

  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractor, BiPredicate<TT, TT> matchFunction,
      MapOver<TT, TT> mapper, ChangeType changeType) {
    return mapCollection(propertyExtractor, propertyExtractor, matchFunction, mapper, changeType);
  }

  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction, MapOver<TT, TT> mapper,
      ChangeType changeType) {

    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMatchFunctionCollectionVisitor<>(matchFunction, changeType, new CollectionChangeFunction<>(mapper)));
    return (M) this;
  }
}
