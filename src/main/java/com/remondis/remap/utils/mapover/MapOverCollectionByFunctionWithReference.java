package com.remondis.remap.utils.mapover;

import static com.remondis.remap.utils.property.ChangeType.ALL;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.CollectionChangeFunction;
import com.remondis.remap.utils.property.visitor.MapOverMatchFunctionCollectionVisitor;

public interface MapOverCollectionByFunctionWithReference<M extends MapOver<R, T>, R, T>
    extends MapOverReference, MapOverCollectionByFunctionWithoutReference<M, R, T> {

  // Entity Reference
  default <TT, ID> M mapCollectionWithReference(Function<T, Collection<TT>> propertyExtractor,
      BiPredicate<TT, TT> matchFunction, Function<TT, ID> idExtractor) {
    return mapCollectionWithReference(propertyExtractor, matchFunction, idExtractor, ALL);
  }

  default <TT, ID> M mapCollectionWithReference(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction,
      Function<TT, ID> idExtractor) {
    return mapCollectionWithReference(propertyExtractorSource, propertyExtractorTarget, matchFunction, idExtractor,
        ALL);
  }

  default <TT, ID> M mapCollectionWithReference(Function<T, Collection<TT>> propertyExtractor,
      BiPredicate<TT, TT> matchFunction, Function<TT, ID> idExtractor, ChangeType changeType) {
    return mapCollectionWithReference(propertyExtractor, propertyExtractor, matchFunction, idExtractor, changeType);
  }

  default <TT, ID> M mapCollectionWithReference(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction,
      Function<TT, ID> idExtractor, ChangeType changeType) {

    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMatchFunctionCollectionVisitor<>(matchFunction, changeType,
            new CollectionChangeFunction<>(getEntityManager(), idExtractor)));
    return (M) this;
  }
}
