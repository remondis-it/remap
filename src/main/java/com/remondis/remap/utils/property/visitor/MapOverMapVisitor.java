package com.remondis.remap.utils.property.visitor;

import com.remondis.remap.utils.mapover.MapOver;
import com.remondis.remap.utils.property.walker.PropertyAccess;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

@RequiredArgsConstructor
public class MapOverMapVisitor<T, TT> implements VisitorFunction<T, Map<Object, TT>> {

  private final boolean addNew;
  private final boolean orphanRemoval;
  private final MapOver<TT, TT> mapper;

  @Override
  public void consume(PropertyAccess<T, Map<Object, TT>> access) {
    Map<Object, TT> sourceMap = access.sourceProperty()
        .get();
    Map<Object, TT> targetMap = access.targetProperty()
        .get();

    Collection<Object> targetCollectionNotHit = new ArrayDeque<>(targetMap.keySet());

    for (Entry<Object, TT> sourceEntry : sourceMap.entrySet()) {
      Object key = sourceEntry.getKey();
      TT sourceValue = sourceEntry.getValue();
      TT targetValue = targetMap.get(sourceEntry.getKey());
      if (targetValue == null) {
        if (addNew) {
          targetMap.put(key, sourceValue);
        }
      } else {
        targetCollectionNotHit.remove(key);
        if (mapper != null) {
          mapper.mapOver(sourceValue, targetValue);
        } else {
          targetMap.put(key, sourceValue);
        }
      }
    }

    if (orphanRemoval) {
      for (Object orphan : targetCollectionNotHit) {
        targetMap.remove(orphan);
      }
    }
  }
}
