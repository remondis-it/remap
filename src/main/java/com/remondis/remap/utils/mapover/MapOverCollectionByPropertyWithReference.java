package com.remondis.remap.utils.mapover;

import static com.remondis.remap.utils.property.ChangeType.ALL;

import java.util.Collection;
import java.util.function.Function;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.CollectionChangeFunction;
import com.remondis.remap.utils.property.visitor.MapOverMatchPropertyCollectionVisitor;

import jakarta.persistence.EntityManager;

public interface MapOverCollectionByPropertyWithReference<M extends MapOver<R, T>, R, T>
    extends MapOverReference, MapOverCollectionByPropertyWithoutReference<M, R, T> {

  // Entity Reference
  default <TT, ID> M mapCollectionWithReference(Function<T, Collection<TT>> propertyExtractor,
      Function<TT, Object> matchProperty, Function<TT, ID> idExtractor) {
    return mapCollectionWithReference(propertyExtractor, matchProperty, idExtractor, ALL);
  }

  default <TT, ID> M mapCollectionWithReference(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty,
      Function<TT, ID> idExtractor) {
    return mapCollectionWithReference(propertyExtractorSource, propertyExtractorTarget, matchProperty, idExtractor,
        ALL);
  }

  default <TT, ID> M mapCollectionWithReference(Function<T, Collection<TT>> propertyExtractor,
      Function<TT, Object> matchProperty, Function<TT, ID> idExtractor, ChangeType changeType) {
    return mapCollectionWithReference(propertyExtractor, propertyExtractor, matchProperty, idExtractor, changeType);
  }

  default <TT, ID> M mapCollectionWithReference(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty,
      Function<TT, ID> idExtractor, ChangeType changeType) {

    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMatchPropertyCollectionVisitor<>(matchProperty, changeType,
            new CollectionChangeFunction<>(getEntityManager(), idExtractor)));
    return (M) this;
  }
}
