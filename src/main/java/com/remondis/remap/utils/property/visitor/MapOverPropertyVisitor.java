package com.remondis.remap.utils.property.visitor;

import com.remondis.remap.utils.mapover.MapOver;
import com.remondis.remap.utils.property.walker.PropertyAccess;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapOverPropertyVisitor<T, TT> implements VisitorFunction<T, TT> {
  private final MapOver<TT, TT> mapper;

  @Override
  public void consume(PropertyAccess<T, TT> access) {
    TT sourceValue = access.sourceProperty()
        .get();
    TT targetValue = access.targetProperty()
        .get();
    mapper.mapOver(sourceValue, targetValue);
  }
}
