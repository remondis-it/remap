package com.remondis.remap.utils.mapover;

import static com.remondis.remap.utils.property.ChangeType.ALL;

import java.util.Map;
import java.util.function.Function;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.MapChangeFunction;
import com.remondis.remap.utils.property.visitor.MapOverMapVisitor;
import com.remondis.remap.utils.property.visitor.VisitorFunction;

public interface MapOverMapWithReference<M extends MapOver<R, T>, R, T>
    extends MapOverReference, MapOverMapWithoutReference<M, R, T> {
  default <TT> M mapMapWithReference(Function<T, Map<Object, TT>> propertyExtractor, Function<TT, Object> idExtractor) {
    return mapMapWithReference(propertyExtractor, idExtractor, ALL);
  }

  default <TT> M mapMapWithReference(Function<T, Map<Object, TT>> propertyExtractorSource,
      Function<T, Map<Object, TT>> propertyExtractorTarget, Function<TT, Object> idExtractor) {
    return mapMapWithReference(propertyExtractorSource, propertyExtractorTarget, idExtractor, ALL);
  }

  default <TT> M mapMapWithReference(Function<T, Map<Object, TT>> propertyExtractor, Function<TT, Object> idExtractor,
      ChangeType changeType) {
    return mapMapWithReference(propertyExtractor, propertyExtractor, idExtractor, changeType);
  }

  default <TT> M mapMapWithReference(Function<T, Map<Object, TT>> propertyExtractorSource,
      Function<T, Map<Object, TT>> propertyExtractorTarget, Function<TT, Object> idExtractor, ChangeType changeType) {

    new MapChangeFunction<>(getEntityManager(), idExtractor);
    Function<T, Map<Object, TT>> propertyExtractorSourceA = propertyExtractorSource;
    Function<T, Map<Object, TT>> propertyExtractorTargetA = propertyExtractorTarget;
    VisitorFunction<T, Map<Object, TT>> biConsumer = new MapOverMapVisitor<>(changeType,
        new MapChangeFunction<>(getEntityManager(), idExtractor));
    getWalker().addProperty(propertyExtractorSourceA, propertyExtractorTargetA, biConsumer);
    return (M) this;
  }
}
