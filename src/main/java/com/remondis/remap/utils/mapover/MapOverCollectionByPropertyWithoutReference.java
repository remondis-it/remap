package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.CollectionChangeFunction;
import com.remondis.remap.utils.property.visitor.MapOverMatchPropertyCollectionVisitor;
import java.util.Collection;
import java.util.function.Function;

import static com.remondis.remap.utils.property.ChangeType.ALL;

public interface MapOverCollectionByPropertyWithoutReference<M extends MapOver<R, T>, R, T> extends
    MapOverCommon<R, T> {

  // Override
  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractor, Function<TT, Object> matchProperty) {
    return mapCollection(propertyExtractor, matchProperty, ALL);
  }

  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchProperty, ALL);
  }

  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractor, Function<TT, Object> matchProperty,
      ChangeType changeType) {
    return mapCollection(propertyExtractor, propertyExtractor, matchProperty, changeType);
  }

  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty, ChangeType changeType) {

    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMatchPropertyCollectionVisitor<>(matchProperty, changeType, new CollectionChangeFunction<>()));
    return (M) this;
  }

  // Mapper
  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractor, Function<TT, Object> matchProperty,
      MapOver<TT, TT> mapper) {
    return mapCollection(propertyExtractor, matchProperty, mapper, ALL);
  }

  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty, MapOver<TT, TT> mapper) {
    return mapCollection(propertyExtractorSource, propertyExtractorTarget, matchProperty, mapper, ALL);
  }

  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractor, Function<TT, Object> matchProperty,
      MapOver<TT, TT> mapper, ChangeType changeType) {
    return mapCollection(propertyExtractor, propertyExtractor, matchProperty, mapper, changeType);
  }

  default <TT> M mapCollection(Function<T, Collection<TT>> propertyExtractorSource,
      Function<T, Collection<TT>> propertyExtractorTarget, Function<TT, Object> matchProperty, MapOver<TT, TT> mapper,
      ChangeType changeType) {

    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMatchPropertyCollectionVisitor<>(matchProperty, changeType, new CollectionChangeFunction<>(mapper)));
    return (M) this;
  }
}
