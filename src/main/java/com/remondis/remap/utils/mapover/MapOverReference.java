package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.walker.BiRecursivePropertyWalker;

import jakarta.persistence.EntityManager;
import lombok.Getter;

import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PROTECTED;

@Getter(PROTECTED)
public class MapOverReference<R, T> extends MapOverCommon<R, T> {

  private final MapOverReference<R, R> root;
  private final EntityManager entityManager;

  @SuppressWarnings("unchecked")
  protected MapOverReference(Class<T> beanType, EntityManager entityManager) {
    super(beanType);
    requireNonNull(entityManager, "entityManager must not be null!");
    this.root = (MapOverReference<R, R>) this;
    this.entityManager = entityManager;
  }

  protected MapOverReference(MapOverReference<R, R> root, BiRecursivePropertyWalker<T, T> walker,
      EntityManager entityManager) {
    super(walker);
    requireNonNull(root, "root must not be null!");
    requireNonNull(entityManager, "entityManager must not be null!");
    this.root = root;
    this.entityManager = entityManager;
  }

  public <TT> MapOverPropertyReferenceBuilder<MapOverReference<R, T>, R, T, TT> mapProperty(
      Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter) {
    return new MapOverPropertyReferenceBuilder<>(this, propertyExtractor, propertyWriter);
  }

  public <TT> MapOverCollectionMatchReferenceBuilder<MapOverReference<R, T>, R, T, TT> mapCollection(
      Function<T, Collection<TT>> propertyExtractor) {
    return new MapOverCollectionMatchReferenceBuilder<>(this, propertyExtractor);
  }

  public <ID, TT> MapOverMapReferenceBuilder<MapOverReference<R, T>, R, T, TT, ID> mapMap(
      Function<T, Map<ID, TT>> propertyExtractor) {
    return new MapOverMapReferenceBuilder<>(this, propertyExtractor);
  }

  @SuppressWarnings("unchecked")
  public <TT> MapOverReference<R, TT> goInto(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter,
      Class<TT> beanType) {
    BiRecursivePropertyWalker<T, TT> goIntoWalker = getWalker().goInto(propertyExtractor, propertyWriter, beanType);
    return new MapOverReference<>(getRoot(), (BiRecursivePropertyWalker<TT, TT>) goIntoWalker, getEntityManager());
  }

  public MapOverReference<R, R> root() {
    return getRoot();
  }

  public MapOver<R, R> build() {
    return getRoot();
  }
}
