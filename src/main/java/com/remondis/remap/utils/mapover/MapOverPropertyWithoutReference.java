package com.remondis.remap.utils.mapover;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.remondis.remap.utils.property.visitor.MapOverOverrideVisitor;
import com.remondis.remap.utils.property.visitor.MapOverPropertyVisitor;
import com.remondis.remap.utils.property.visitor.VisitorFunction;

public interface MapOverPropertyWithoutReference<M extends MapOver<R, T>, R, T> extends MapOverBase<R, T> {
  default <TT> M mapProperty(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter) {
    return addPropertyAction(propertyExtractor, propertyWriter, new MapOverOverrideVisitor<>());
  }

  default <TT> M mapProperty(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter,
      MapOver<TT, TT> mapper) {
    return addPropertyAction(propertyExtractor, propertyWriter, new MapOverPropertyVisitor<>(mapper));
  }

  default <TT> M addPropertyAction(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter,
      VisitorFunction<T, TT> action) {
    getWalker().addProperty(propertyExtractor, propertyWriter, action);
    return (M) this;
  }
}
