package com.remondis.remap.utils.property.visitor;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;

import com.remondis.remap.utils.property.ChangeType;
import com.remondis.remap.utils.property.change.CollectionChangeFunction;
import com.remondis.remap.utils.property.match.MatchMethod;
import com.remondis.remap.utils.property.walker.PropertyAccess;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapOverCollectionVisitor<T, TT, ID> implements VisitorFunction<T, Collection<TT>> {

  private final MatchMethod<TT> matchMethod;
  private final ChangeType changeType;
  private final CollectionChangeFunction<TT, ID> changeFunction;

  @Override
  public void consume(PropertyAccess<T, Collection<TT>> access) {
    if (matchMethod.getMatchPropertyExtractor() != null) {
      consumeWithMatchProperty(access, matchMethod.getMatchPropertyExtractor());
    } else if (matchMethod.getMatchFunction() != null) {
      consumeWithMatchFunction(access, matchMethod.getMatchFunction());
    }
  }

  public void consumeWithMatchProperty(PropertyAccess<T, Collection<TT>> access,
      Function<TT, Object> matchPropertyExtractor) {
    Collection<TT> sourceCollection = access.sourceProperty()
        .get();
    Collection<TT> targetCollection = access.targetProperty()
        .get();

    Map<Object, TT> targetMap = targetCollection.stream()
        .collect(toMap(matchPropertyExtractor, identity()));

    for (TT sourceValue : sourceCollection) {
      Object key = matchPropertyExtractor.apply(sourceValue);
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

  public void consumeWithMatchFunction(PropertyAccess<T, Collection<TT>> access, BiPredicate<TT, TT> matchFunction) {
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
