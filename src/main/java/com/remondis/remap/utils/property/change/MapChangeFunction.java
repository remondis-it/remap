package com.remondis.remap.utils.property.change;

import java.util.Map;
import java.util.function.Function;

import com.remondis.remap.utils.mapover.MapOver;

import jakarta.persistence.EntityManager;

public class MapChangeFunction<TT, ID> {

  private final MapOver<TT, TT> mapper;
  private final EntityManager entityManager;
  private final Function<TT, ID> idExtractor;

  public MapChangeFunction() {
    this.mapper = null;
    this.entityManager = null;
    this.idExtractor = null;
  }

  public MapChangeFunction(MapOver<TT, TT> mapper) {
    this.mapper = mapper;
    this.entityManager = null;
    this.idExtractor = null;
  }

  public MapChangeFunction(EntityManager entityManager, Function<TT, ID> idExtractor) {
    this.mapper = null;
    this.entityManager = entityManager;
    this.idExtractor = idExtractor;
  }

  public void changeMap(TT sourceValue, TT targetValue, Map<ID, TT> targetCollection, ID key) {
    if (mapper != null) {
      mapper.mapOver(sourceValue, targetValue);
    } else {
      targetCollection.remove(key);
      addToMap(sourceValue, targetCollection, key);
    }
  }

  @SuppressWarnings("unchecked")
  public void addToMap(TT sourceValue, Map<ID, TT> targetCollection, ID key) {
    if (entityManager != null && idExtractor != null) {
      targetCollection.put(key,
          (TT) entityManager.getReference(sourceValue.getClass(), idExtractor.apply(sourceValue)));
    } else {
      targetCollection.put(key, sourceValue);
    }
  }
}
