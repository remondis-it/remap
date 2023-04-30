package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.CollectionChangeFunction;
import com.remondis.remap.utils.property.visitor.MapOverMatchPropertyCollectionVisitor;
import jakarta.persistence.EntityManager;

import java.util.Collection;
import java.util.function.Function;

import static com.remondis.remap.utils.property.ChangeType.ALL;

public interface MapOverCollectionByProperty<R, T> extends MapOverBase<R, T> {

  // Override
  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      Function<TT, Object> matchProperty) {
    return mapCollection(propertyExtractor, matchProperty, ALL);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchProperty, ALL);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      Function<TT, Object> matchProperty, ChangeType changeType) {
    return mapCollection(propertyExtractor, propertyExtractor, matchProperty, changeType);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty, ChangeType changeType) {

    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMatchPropertyCollectionVisitor<>(matchProperty, changeType, new CollectionChangeFunction<>()));
    return (MapOver<R, T>) this;
  }

  // Mapper
  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      Function<TT, Object> matchProperty, MapOver<TT, TT> mapper) {
    return mapCollection(propertyExtractor, matchProperty, mapper, ALL);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty, MapOver<TT, TT> mapper) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchProperty, mapper, ALL);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      Function<TT, Object> matchProperty, MapOver<TT, TT> mapper, ChangeType changeType) {
    return mapCollection(propertyExtractor, propertyExtractor, matchProperty, mapper, changeType);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty, MapOver<TT, TT> mapper,
      ChangeType changeType) {

    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMatchPropertyCollectionVisitor<>(matchProperty, changeType, new CollectionChangeFunction<>(mapper)));
    return (MapOver<R, T>) this;
  }

  // Entity Reference
  default <TT, ID> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      Function<TT, Object> matchProperty, EntityManager entityManager, Function<TT, ID> idExtractor) {
    return mapCollection(propertyExtractor, matchProperty, entityManager, idExtractor, ALL);
  }

  default <TT, ID> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty,
      EntityManager entityManager, Function<TT, ID> idExtractor) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchProperty, entityManager, idExtractor,
        ALL);
  }

  default <TT, ID> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      Function<TT, Object> matchProperty, EntityManager entityManager, Function<TT, ID> idExtractor,
      ChangeType changeType) {
    return mapCollection(propertyExtractor, propertyExtractor, matchProperty, entityManager, idExtractor, changeType);
  }

  default <TT, ID> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty,
      EntityManager entityManager, Function<TT, ID> idExtractor, ChangeType changeType) {

    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMatchPropertyCollectionVisitor<>(matchProperty, changeType,
            new CollectionChangeFunction<>(entityManager, idExtractor)));
    return (MapOver<R, T>) this;
  }
}
