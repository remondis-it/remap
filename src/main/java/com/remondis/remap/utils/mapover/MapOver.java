package com.remondis.remap.utils.mapover;

import jakarta.persistence.EntityManager;

/**
 * <p>
 * A util class that supports mapping of objects of the same type while only specified properties are taken from source
 * and will be overridden in the target object. This makes it easier to use attached JPA entities and only map over
 * changes made
 * in the source object.
 * Handling attached entities with RE-Map is a little bit tricky when it comes to update operations, since RE-Map always
 * creates a new object and the map-into method in the mapper does not perform a real recursive map-into.
 * </p>
 * <p>
 * Using the MapOver utility makes it possible to apply the following update strategy on attached entities:
 *
 * <pre>
 * REST -->  DTO  -->  RE-Map  --> new Entity instance     (detached)
 *                                                             *
 *                                                             |
 *                                                         Map-over selected attributes
 *                                                         to perform a partial update
 *                                                         on the attached entity+
 *                                                             |
 *                                                             |
 *                                                             V
 * JPA-Repository --> findById --> Entity instance from DB (attached)  --> em.update(entity);
 * </pre>
 * </p>
 *
 * @param <T> The root bean type
 */
public interface MapOver<R, T> extends MapOverBase<R, T> {

  static <R> MapOverWithoutReference<R, R> create(Class<R> beanType) {
    return new MapOverWithoutReference<>(beanType);
  }

  static <R> MapOverWithReference<R, R> create(Class<R> beanType, EntityManager entityManager) {
    return new MapOverWithReference<>(beanType, entityManager);
  }

  default MapOver<R, R> build() {
    return getRoot();
  }

  default void mapOver(T source, T target) {
    getWalker().execute(source, target);
  }
}
