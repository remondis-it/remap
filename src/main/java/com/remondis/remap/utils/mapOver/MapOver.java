package com.remondis.remap.utils.mapOver;

import java.util.function.Function;

import com.remondis.remap.utils.propertywalker.BiRecursivePropertyWalker;

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
 * @param <R> The root bean type
 */
public class MapOver<R> {

  private BiRecursivePropertyWalker<R, R> walker;

  private MapOver(Class<R> beanType) {
    super();
    this.walker = BiRecursivePropertyWalker.create(beanType);
  }

  public <T> MapOver<R> mapProperty(Function<R, T> propertyExtractor) {
    // BiConsumer<T, T> propertyConsumer = (s, t) -> {
    //
    // };
    // walker.addProperty(propertyExtractor, propertyConsumer);
    return this;
  }

  public static <R> MapOver<R> create(Class<R> beanType) {
    return new MapOver<>(beanType);
  }

}
