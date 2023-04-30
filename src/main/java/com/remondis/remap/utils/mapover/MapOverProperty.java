package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.visitor.MapOverPropertyVisitor;
import com.remondis.remap.utils.property.visitor.MapOverReferenceVisitor;
import com.remondis.remap.utils.property.visitor.MapOverOverrideVisitor;
import com.remondis.remap.utils.property.visitor.VisitorFunction;
import jakarta.persistence.EntityManager;

import java.util.function.BiConsumer;
import java.util.function.Function;

public interface MapOverProperty<R, T> extends MapOverBase<R, T> {
  default <TT> MapOver<R, T> mapProperty(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter) {
    return addPropertyAction(propertyExtractor, propertyWriter, new MapOverOverrideVisitor<>());
  }

  default <TT> MapOver<R, T> mapProperty(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter,
      MapOver<TT, TT> mapper) {
    return addPropertyAction(propertyExtractor, propertyWriter, new MapOverPropertyVisitor<>(mapper));
  }

  default <TT, ID> MapOver<R, T> mapProperty(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter,
      EntityManager entityManager, Function<TT, ID> idExtractor) {
    return addPropertyAction(propertyExtractor, propertyWriter,
        new MapOverReferenceVisitor<>(entityManager, idExtractor));
  }

  default <TT> MapOver<R, T> addPropertyAction(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter,
      VisitorFunction<T, TT> action) {
    getWalker().addProperty(propertyExtractor, propertyWriter, action);
    return (MapOver<R, T>) this;
  }
}
