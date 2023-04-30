package com.remondis.remap.utils.mapover;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.remondis.remap.utils.property.visitor.MapOverReferenceVisitor;

public interface MapOverPropertyWithReference<M extends MapOver<R, T>, R, T>
    extends MapOverReference, MapOverPropertyWithoutReference<M, R, T> {
  default <TT, ID> M mapPropertyWithReference(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter,
      Function<TT, ID> idExtractor) {

    getWalker().addProperty(propertyExtractor, propertyWriter,
        new MapOverReferenceVisitor<>(getEntityManager(), idExtractor));
    return (M) this;
  }
}
