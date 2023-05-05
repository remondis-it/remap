package com.remondis.remap.utils.mapover;

import static com.remondis.remap.utils.property.ChangeType.ALL;

import java.util.Map;
import java.util.function.Function;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.MapChangeFunction;
import com.remondis.remap.utils.property.visitor.MapOverMapVisitor;
import com.remondis.remap.utils.property.visitor.VisitorFunction;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapOverMapBuilder<M extends MapOverCommon<R, T>, R, T, TT, ID> {
  protected final M mapOver;
  protected final Function<T, Map<ID, TT>> propertyExtractor;

  public M byOverwrite() {
    return byOverwrite(ALL);
  }

  public M byOverwrite(ChangeType changeType) {
    return byAction(new MapOverMapVisitor<>(changeType, new MapChangeFunction<>()));
  }

  public M byMapper(MapOver<TT, TT> mapper) {
    return byMapper(mapper, ALL);
  }

  public M byMapper(MapOver<TT, TT> mapper, ChangeType changeType) {
    return byAction(new MapOverMapVisitor<>(changeType, new MapChangeFunction<>(mapper)));
  }

  public M byAction(VisitorFunction<T, Map<ID, TT>> action) {
    mapOver.getWalker()
        .addProperty(propertyExtractor, action);
    return mapOver;
  }
}
