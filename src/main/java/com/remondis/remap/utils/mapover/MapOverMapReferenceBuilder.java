package com.remondis.remap.utils.mapover;

import static com.remondis.remap.utils.property.ChangeType.ALL;

import java.util.Map;
import java.util.function.Function;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.MapChangeFunction;
import com.remondis.remap.utils.property.visitor.MapOverMapVisitor;

public class MapOverMapReferenceBuilder<M extends MapOverReference<R, T>, R, T, TT, ID>
    extends MapOverMapBuilder<M, R, T, TT, ID> {

  public MapOverMapReferenceBuilder(M mapOver, Function<T, Map<ID, TT>> propertyExtractor) {
    super(mapOver, propertyExtractor);
  }

  public M byReference(Function<TT, ID> idExtractor) {
    return byReference(idExtractor, ALL);
  }

  public M byReference(Function<TT, ID> idExtractor, ChangeType changeType) {
    MapChangeFunction<TT, ID> byReference = new MapChangeFunction<>(mapOver.getEntityManager(), idExtractor);
    mapOver.getWalker()
        .addProperty(propertyExtractor, new MapOverMapVisitor<>(changeType, byReference));
    return mapOver;
  }
}
