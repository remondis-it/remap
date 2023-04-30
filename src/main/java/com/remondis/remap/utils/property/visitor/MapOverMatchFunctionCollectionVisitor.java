package com.remondis.remap.utils.property.visitor;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.function.BiPredicate;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.CollectionChangeFunction;
import com.remondis.remap.utils.property.walker.PropertyAccess;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapOverMatchFunctionCollectionVisitor<T, TT, ID> implements VisitorFunction<T, Collection<TT>> {

  private final BiPredicate<TT, TT> matchFunction;
  private final ChangeType changeType;
  private final CollectionChangeFunction<TT, ID> changeFunction;

  @Override
  public void consume(PropertyAccess<T, Collection<TT>> access) {
    Collection<TT> sourceCollection = access.sourceProperty()
        .get();
    Collection<TT> targetCollection = access.targetProperty()
        .get();

    Collection<TT> targetCollectionNotHit = new ArrayDeque<>(targetCollection);

    for (TT sourceValue : sourceCollection) {
      boolean hitSource = false;
      for (TT targetValue : targetCollection) {
        if (matchFunction.test(sourceValue, targetValue)) {
          changeFunction.changeCollection(sourceValue, targetValue, targetCollection);
          targetCollectionNotHit.remove(targetValue);
          hitSource = true;
          break;
        }
      }
      if (!hitSource && changeType.isAddNew()) {
        changeFunction.addToCollection(sourceValue, targetCollection);
      }
    }

    if (changeType.isRemoveOrphans()) {
      for (TT orphan : targetCollectionNotHit) {
        targetCollection.remove(orphan);
      }
    }
  }
}
