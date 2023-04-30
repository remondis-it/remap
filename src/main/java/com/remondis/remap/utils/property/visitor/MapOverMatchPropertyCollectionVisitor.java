package com.remondis.remap.utils.property.visitor;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.CollectionChangeFunction;
import com.remondis.remap.utils.property.walker.PropertyAccess;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapOverMatchPropertyCollectionVisitor<T, TT, ID> implements VisitorFunction<T, Collection<TT>> {

  private final Function<TT, Object> matchProperty;
  private final ChangeType changeType;
  private final CollectionChangeFunction<TT, ID> changeFunction;

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
      if (targetValue == null && changeType.isAddNew()) {
        changeFunction.addToCollection(sourceValue, targetCollection);
      } else {
        changeFunction.changeCollection(sourceValue, targetValue, targetCollection);
        targetMap.remove(key);
      }
    }

    if (changeType.isRemoveOrphans()) {
      for (TT orphan : targetMap.values()) {
        targetCollection.remove(orphan);
      }
    }
  }
}
