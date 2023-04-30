package com.remondis.remap.utils.property.visitor;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.MapChangeFunction;
import com.remondis.remap.utils.property.walker.PropertyAccess;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

@RequiredArgsConstructor
public class MapOverMapVisitor<T, TT, ID> implements VisitorFunction<T, Map<ID, TT>> {

  private final ChangeType changeType;
  private final MapChangeFunction<TT, ID> changeFunction;

  @Override
  public void consume(PropertyAccess<T, Map<ID, TT>> access) {
    Map<ID, TT> sourceMap = access.sourceProperty()
        .get();
    Map<ID, TT> targetMap = access.targetProperty()
        .get();

    Collection<ID> targetCollectionNotHit = new ArrayDeque<>(targetMap.keySet());

    for (Entry<ID, TT> sourceEntry : sourceMap.entrySet()) {
      ID key = sourceEntry.getKey();
      TT sourceValue = sourceEntry.getValue();
      TT targetValue = targetMap.get(sourceEntry.getKey());
      if (targetValue == null) {
        if (changeType.isAddNew()) {
          changeFunction.addToMap(sourceValue, targetMap, key);
        }
      } else {
        targetCollectionNotHit.remove(key);
        changeFunction.changeMap(sourceValue, targetValue, targetMap, key);
      }
    }

    if (changeType.isRemoveOrphans()) {
      for (Object orphan : targetCollectionNotHit) {
        targetMap.remove(orphan);
      }
    }
  }
}
