package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.MapChangeFunction;
import com.remondis.remap.utils.property.visitor.MapOverMapVisitor;

import java.util.Map;
import java.util.function.Function;

import static com.remondis.remap.utils.property.ChangeType.ALL;

public interface MapOverMapWithoutReference<M extends MapOver<R, T>, R, T> extends MapOverBase<R, T> {

  // Override
  default <TT> M mapMap(Function<T, Map<Object, TT>> propertyExtractor) {
    return mapMap(propertyExtractor, ALL);
  }

  default <TT> M mapMap(Function<T, Map<Object, TT>> propertyExtractorSource,
      Function<T, Map<Object, TT>> propertyExtractorTarget) {
    return mapMap(propertyExtractorSource, propertyExtractorTarget, ALL);
  }

  default <TT> M mapMap(Function<T, Map<Object, TT>> propertyExtractor, ChangeType changeType) {
    return mapMap(propertyExtractor, propertyExtractor, changeType);
  }

  default <TT> M mapMap(Function<T, Map<Object, TT>> propertyExtractorSource,
      Function<T, Map<Object, TT>> propertyExtractorTarget, ChangeType changeType) {
    return mapMap(propertyExtractorSource, propertyExtractorTarget, null, changeType);
  }

  // Mapper
  default <TT> M mapMap(Function<T, Map<Object, TT>> propertyExtractor, MapOver<TT, TT> mapper) {
    return mapMap(propertyExtractor, mapper, ALL);
  }

  default <TT> M mapMap(Function<T, Map<Object, TT>> propertyExtractorSource,
      Function<T, Map<Object, TT>> propertyExtractorTarget, MapOver<TT, TT> mapper) {
    return mapMap(propertyExtractorSource, propertyExtractorTarget, mapper, ALL);
  }

  default <TT> M mapMap(Function<T, Map<Object, TT>> propertyExtractor, MapOver<TT, TT> mapper, ChangeType changeType) {
    return mapMap(propertyExtractor, propertyExtractor, mapper, changeType);
  }

  default <TT> M mapMap(Function<T, Map<Object, TT>> propertyExtractorSource,
      Function<T, Map<Object, TT>> propertyExtractorTarget, MapOver<TT, TT> mapper, ChangeType changeType) {
    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMapVisitor<>(changeType, new MapChangeFunction<>(mapper)));
    return (M) this;
  }
}
