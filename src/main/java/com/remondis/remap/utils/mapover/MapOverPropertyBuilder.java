package com.remondis.remap.utils.mapover;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.remondis.remap.utils.property.visitor.MapOverOverrideVisitor;
import com.remondis.remap.utils.property.visitor.MapOverPropertyVisitor;
import com.remondis.remap.utils.property.visitor.VisitorFunction;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapOverPropertyBuilder<M extends MapOverCommon<R, T>, R, T, TT> {
  protected final M mapOver;
  protected final Function<T, TT> propertyExtractor;
  protected final BiConsumer<T, TT> propertyWriter;

  public M byOverwrite() {
    return byAction(new MapOverOverrideVisitor<>());
  }

  public M byMapper(MapOver<TT, TT> mapper) {
    return byAction(new MapOverPropertyVisitor<>(mapper));
  }

  public M byAction(VisitorFunction<T, TT> action) {
    mapOver.getWalker()
        .addProperty(propertyExtractor, propertyWriter, action);
    return mapOver;
  }
}
