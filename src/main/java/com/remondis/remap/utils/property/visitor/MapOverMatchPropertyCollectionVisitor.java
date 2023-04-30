package com.remondis.remap.utils.property.visitor;

import com.remondis.remap.utils.mapover.MapOver;
import com.remondis.remap.utils.property.walker.PropertyAccess;
import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@RequiredArgsConstructor
public class MapOverMatchPropertyCollectionVisitor<T, TT> implements VisitorFunction<T, Collection<TT>> {

  private final Function<TT, Object> matchProperty;
  private final boolean addNew;
  private final boolean orphanRemoval;
  private final MapOver<TT, TT> mapper;

  @Override
  public void consume(PropertyAccess<T, Collection<TT>> access) {
    Collection<TT> sourceCollection = access.sourceProperty()
        .get();
    Collection<TT> targetCollection = access.targetProperty()
        .get();

    Map<Object, TT> targetMap = targetCollection.stream()
        .collect(toMap(matchProperty, identity()));

    for (TT sourceValue : sourceCollection) {
      Object key = matchProperty.apply(sourceValue);
      TT targetValue = targetMap.get(key);
      if (targetValue == null && addNew) {
        targetCollection.add(sourceValue);
      } else {
        if (mapper != null) {
          mapper.mapOver(sourceValue, targetValue);
        } else {
          targetCollection.remove(targetValue);
          targetCollection.add(sourceValue);
        }
        targetMap.remove(key);
      }
    }

    if (orphanRemoval) {
      for (TT orphan : targetMap.values()) {
        targetCollection.remove(orphan);
      }
    }
  }
}
