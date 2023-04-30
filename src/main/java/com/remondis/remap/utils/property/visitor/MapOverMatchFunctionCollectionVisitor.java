package com.remondis.remap.utils.property.visitor;

import com.remondis.remap.utils.mapover.MapOver;
import com.remondis.remap.utils.property.walker.PropertyAccess;
import lombok.RequiredArgsConstructor;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.function.BiPredicate;

@RequiredArgsConstructor
public class MapOverMatchFunctionCollectionVisitor<T, TT> implements VisitorFunction<T, Collection<TT>> {

  private final BiPredicate<TT, TT> matchFunction;
  private final boolean addNew;
  private final boolean orphanRemoval;
  private final MapOver<TT, TT> mapper;

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
          if (mapper != null) {
            mapper.mapOver(sourceValue, targetValue);
          } else {
            targetCollection.remove(targetValue);
            targetCollection.add(sourceValue);
          }
          targetCollectionNotHit.remove(targetValue);
          hitSource = true;
          break;
        }
      }
      if (!hitSource && addNew) {
        targetCollection.add(sourceValue);
      }
    }

    if (orphanRemoval) {
      for (TT orphan : targetCollectionNotHit) {
        targetCollection.remove(orphan);
      }
    }
  }
}
