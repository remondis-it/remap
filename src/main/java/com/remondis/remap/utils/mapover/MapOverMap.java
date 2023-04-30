package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.visitor.MapOverMapVisitor;

import java.util.Map;
import java.util.function.Function;

public interface MapOverMap<R, T> extends MapOverBase<R, T> {
  default <TT> MapOver<R, T> mapMap(Function<T, Map<Object, TT>> propertyExtractor) {
    return mapMap(propertyExtractor, true, true);
  }

  default <TT> MapOver<R, T> mapMap(Function<T, Map<Object, TT>> propertyExtractorSource,
      Function<T, Map<Object, TT>> propertyExtractorTarget) {
    return mapMap(propertyExtractorSource, propertyExtractorTarget, true, true);
  }

  default <TT> MapOver<R, T> mapMap(Function<T, Map<Object, TT>> propertyExtractor, boolean addNew,
      boolean orphanRemoval) {
    return mapMap(propertyExtractor, propertyExtractor, addNew, orphanRemoval);
  }

  default <TT> MapOver<R, T> mapMap(Function<T, Map<Object, TT>> propertyExtractorSource,
      Function<T, Map<Object, TT>> propertyExtractorTarget, boolean addNew, boolean orphanRemoval) {
    return mapMap(propertyExtractorSource, propertyExtractorTarget, null, addNew, orphanRemoval);
  }

  default <TT> MapOver<R, T> mapMap(Function<T, Map<Object, TT>> propertyExtractor, MapOver<TT, TT> mapper) {
    return mapMap(propertyExtractor, mapper, true, true);
  }

  default <TT> MapOver<R, T> mapMap(Function<T, Map<Object, TT>> propertyExtractorSource,
      Function<T, Map<Object, TT>> propertyExtractorTarget, MapOver<TT, TT> mapper) {
    return mapMap(propertyExtractorSource, propertyExtractorTarget, mapper, true, true);
  }

  default <TT> MapOver<R, T> mapMap(Function<T, Map<Object, TT>> propertyExtractor, MapOver<TT, TT> mapper,
      boolean addNew, boolean orphanRemoval) {
    return mapMap(propertyExtractor, propertyExtractor, mapper, addNew, orphanRemoval);
  }

  default <TT> MapOver<R, T> mapMap(Function<T, Map<Object, TT>> propertyExtractorSource,
      Function<T, Map<Object, TT>> propertyExtractorTarget, MapOver<TT, TT> mapper, boolean addNew,
      boolean orphanRemoval) {
    getWalker().addProperty(propertyExtractorSource, propertyExtractorTarget,
        new MapOverMapVisitor<>(addNew, orphanRemoval, mapper));
    return (MapOver<R, T>) this;
  }
}
