package com.remondis.remap.utils.property.change;

import com.remondis.remap.utils.mapover.MapOver;
import jakarta.persistence.EntityManager;
import java.util.Collection;
import java.util.function.Function;

public class CollectionChangeFunction<TT, ID> {

  private final MapOver<TT, TT> mapper;
  private final EntityManager entityManager;
  private final Function<TT, ID> idExtractor;

  public CollectionChangeFunction() {
    this.mapper = null;
    this.entityManager = null;
    this.idExtractor = null;
  }

  public CollectionChangeFunction(MapOver<TT, TT> mapper) {
    this.mapper = mapper;
    this.entityManager = null;
    this.idExtractor = null;
  }

  public CollectionChangeFunction(EntityManager entityManager, Function<TT, ID> idExtractor) {
    this.mapper = null;
    this.entityManager = entityManager;
    this.idExtractor = idExtractor;
  }

  public void changeCollection(TT sourceValue, TT targetValue, Collection<TT> targetCollection) {
    if (mapper != null) {
      mapper.mapOver(sourceValue, targetValue);
    } else {
      targetCollection.remove(targetValue);
      addToCollection(sourceValue, targetCollection);
    }
  }

  @SuppressWarnings("unchecked")
  public void addToCollection(TT sourceValue, Collection<TT> targetCollection) {
    if (entityManager != null && idExtractor != null) {
      targetCollection.add((TT) entityManager.getReference(sourceValue.getClass(), idExtractor.apply(sourceValue)));
    } else {
      targetCollection.add(sourceValue);
    }
  }
}
