package com.remondis.remap.utils.mapOver;

import java.util.function.BiConsumer;
import java.util.function.Function;

import com.remondis.remap.utils.propertywalker.BiRecursivePropertyWalker;
import com.remondis.remap.utils.propertywalker.PropertyAccess;
import com.remondis.remap.utils.propertywalker.VisitorFunction;

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
public class MapOver<R, T> {

  private MapOver<R, R> root;

  private BiRecursivePropertyWalker<T, T> walker;

  private MapOver(Class<T> beanType) {
    super();
    this.root = (MapOver<R, R>) this;
    this.walker = BiRecursivePropertyWalker.create(beanType);
  }

  private MapOver(MapOver<R, R> root, BiRecursivePropertyWalker<T, T> walker) {
    super();
    this.root = root;
    this.walker = walker;
  }

  public <TT> MapOver<R, T> mapProperty(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter) {
    walker.addProperty(propertyExtractor, propertyWriter, propertyVisitor());
    return this;
  }

  public <TT> MapOver<R, TT> goInto(Function<T, TT> propertyExtractor, BiConsumer<T, TT> propertyWriter,
      Class<TT> beanType) {
    BiRecursivePropertyWalker<T, TT> goIntoWalker = walker.goInto(propertyExtractor, propertyWriter, beanType);
    return new MapOver<R, TT>(root, (BiRecursivePropertyWalker<TT, TT>) goIntoWalker);
  }

  public static <R> MapOver<R, R> create(Class<R> beanType) {
    return new MapOver<>(beanType);
  }

  private <TT> VisitorFunction<T, TT> propertyVisitor() {
    return new VisitorFunction<T, TT>() {

      @Override
      public void consume(PropertyAccess<T, TT> access) {
        TT sourceValue = access.sourceProperty()
            .get();
        access.targetProperty()
            .set(sourceValue);
      }
    };
  }

  public MapOver<R, R> build() {
    return root;
  }

  public void mapOver(T source, T target) {
    walker.execute(source, target);
  }

}
