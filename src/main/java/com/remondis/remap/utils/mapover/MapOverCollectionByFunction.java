package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.CollectionChangeFunction;
import com.remondis.remap.utils.property.visitor.MapOverMatchFunctionCollectionVisitor;
import jakarta.persistence.EntityManager;

import java.util.Collection;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static com.remondis.remap.utils.property.ChangeType.ALL;

public interface MapOverCollectionByFunction<R, T> extends MapOverBase<R, T> {

  // Override
  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      BiPredicate<TT, TT> matchFunction) {
    return mapCollection(propertyExtractor, matchFunction, ALL);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchFunction, ALL);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      BiPredicate<TT, TT> matchFunction, ChangeType changeType) {
    return mapCollection(propertyExtractor, propertyExtractor, matchFunction, changeType);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction, ChangeType changeType) {

    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMatchFunctionCollectionVisitor<>(matchFunction, changeType, new CollectionChangeFunction<>()));
    return (MapOver<R, T>) this;
  }

  // Mapping
  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      BiPredicate<TT, TT> matchFunction, MapOver<TT, TT> mapper) {
    return mapCollection(propertyExtractor, matchFunction, mapper, ALL);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction, MapOver<TT, TT> mapper) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchFunction, mapper, ALL);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      BiPredicate<TT, TT> matchFunction, MapOver<TT, TT> mapper, ChangeType changeType) {
    return mapCollection(propertyExtractor, propertyExtractor, matchFunction, mapper, changeType);
  }

  default <TT> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction, MapOver<TT, TT> mapper,
      ChangeType changeType) {

    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMatchFunctionCollectionVisitor<>(matchFunction, changeType, new CollectionChangeFunction<>(mapper)));
    return (MapOver<R, T>) this;
  }

  // Entity Reference
  default <TT, ID> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      BiPredicate<TT, TT> matchFunction, EntityManager entityManager, Function<TT, ID> idExtractor) {
    return mapCollection(propertyExtractor, matchFunction, entityManager, idExtractor, ALL);
  }

  default <TT, ID> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction,
      EntityManager entityManager, Function<TT, ID> idExtractor) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchFunction, entityManager, idExtractor,
        ALL);
  }

  default <TT, ID> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractor,
      BiPredicate<TT, TT> matchFunction, EntityManager entityManager, Function<TT, ID> idExtractor,
      ChangeType changeType) {
    return mapCollection(propertyExtractor, propertyExtractor, matchFunction, entityManager, idExtractor, changeType);
  }

  default <TT, ID> MapOver<R, T> mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, BiPredicate<TT, TT> matchFunction,
      EntityManager entityManager, Function<TT, ID> idExtractor, ChangeType changeType) {

    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMatchFunctionCollectionVisitor<>(matchFunction, changeType,
            new CollectionChangeFunction<>(entityManager, idExtractor)));
    return (MapOver<R, T>) this;
  }
}
