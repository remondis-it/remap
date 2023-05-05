package com.remondis.remap.utils.mapover;

import com.remondis.remap.utils.property.walker.BiRecursivePropertyWalker;

import jakarta.persistence.EntityManager;
import lombok.Getter;

import java.util.function.BiConsumer;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

@Getter
public class MapOverWithReference<R, T> extends MapOverCommon<R, T> {

  private final MapOverWithReference<R, R> root;
  private final EntityManager entityManager;

  @SuppressWarnings("unchecked")
  protected MapOverWithReference(Class<T> beanType, EntityManager entityManager) {
    super(beanType);
    requireNonNull(entityManager, "entityManager must not be null!");
    this.root = (MapOverWithReference<R, R>) this;
    this.entityManager = entityManager;
  }

  protected MapOverWithReference(MapOverWithReference<R, R> root, BiRecursivePropertyWalker<T, T> walker,
      EntityManager entityManager) {
    super(walker);
    requireNonNull(root, "root must not be null!");
    requireNonNull(entityManager, "entityManager must not be null!");
    this.root = root;
    this.entityManager = entityManager;
  }

  public <TT> MapOverPropertyReferenceBuilder<MapOverWithReference<R, T>, R, T, TT> mapProperty(
      Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter) {
    return new MapOverPropertyReferenceBuilder<>(this, propertyExtractor, propertyWriter);
  }

  @SuppressWarnings("unchecked")
  public <TT> MapOverWithReference<R, TT> goInto(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter,
      Class<TT> beanType) {
    BiRecursivePropertyWalker<T, TT> goIntoWalker = getWalker().goInto(propertyExtractor, propertyWriter, beanType);
    return new MapOverWithReference<>(getRoot(), (BiRecursivePropertyWalker<TT, TT>) goIntoWalker, getEntityManager());
  }

  public MapOverWithReference<R, R> root() {
    return getRoot();
  }
}
