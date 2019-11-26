package com.remondis.remap;

import static com.remondis.remap.ReassignTransformation.isCollection;
import static com.remondis.remap.ReassignTransformation.isMap;
import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

import java.beans.PropertyDescriptor;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

/**
 * This util creates {@link Mapper}s used for creating deep-copies of objects. It analyzes the type's properties to
 * create identity mappers. For immutable objects or non-Java Beans you can use
 * {@link DeepCopyMapperBuilder#copyReference(Class)} or
 * {@link DeepCopyMapperBuilder#copyWith(Class, Function)} to configure alternative copy functions.
 *
 * @author schuettec
 *
 */
public final class DeepCopy {

  private DeepCopy() {

  }

  /**
   * This is the builder class used to configure a deep-copy {@link Mapper}.
   *
   * @param <T> The type to deep-copy.
   *
   * @author schuettec
   */
  public static class DeepCopyMapperBuilder<T> {
    private Class<T> type;
    private Map<Class<?>, TypeMapping<?, ?>> typeMappings;

    private DeepCopyMapperBuilder(Class<T> type) {
      super();
      this.type = type;
      this.typeMappings = new Hashtable<Class<?>, TypeMapping<?, ?>>();
    }

    /**
     * This method excludes a type from a deep-copy, so that instances of this type are copied by using the same object
     * reference. This can be used for immutable value objects.
     *
     * @param <S> The type.
     * @param typeToCopyByReference The type to exclude from deep-copy.
     * @return Returns this {@link DeepCopyMapperBuilder} for further configuration.
     */
    public <S> DeepCopyMapperBuilder<T> copyReference(Class<S> typeToCopyByReference) {
      requireNonNull(typeToCopyByReference, "typeToCopyByReference must not be null!");
      typeMappings.put(typeToCopyByReference, TypeMapping.from(typeToCopyByReference)
          .to(typeToCopyByReference)
          .applying(identity()));
      return this;
    }

    /**
     * Adds a type mapping used to create a copy from non-Java Beans. Use this method for objects that cannot be
     * constructed via parameterless default constructor or parameters that have read-only properties.
     *
     * @param <S> The type.
     * @param nonBeanType The type to copy by function.
     * @param copyFunction The {@link Function} to copy objects of the respective type.
     * @return Returns this {@link DeepCopyMapperBuilder} for further configuration.
     */
    public <S> DeepCopyMapperBuilder<T> copyWith(Class<S> nonBeanType, Function<S, S> copyFunction) {
      requireNonNull(nonBeanType, "nonBeanType must not be null!");
      requireNonNull(copyFunction, "copyFunction must not be null!");
      TypeMapping<S, S> mapping = TypeMapping.from(nonBeanType)
          .to(nonBeanType)
          .applying(copyFunction);
      typeMappings.put(nonBeanType, mapping);
      return this;
    }

    /**
     * @return Returns the resulting deep-copy {@link Mapper}.
     */
    public Mapper<T, T> getMapper() {
      try {
        return deepCopyMapperCached(type, typeMappings, new Hashtable<>());
      } catch (MappingException e) {
        throw new MappingException(
            "Building a deep-copy mapper failed. Please check that copy functions were registered for all non-Java Bean types.",
            e);
      }
    }
  }

  /**
   * Creates a new builder to build and configure a deep-copy {@link Mapper}.
   *
   * @param <T> The type to deep-copy.
   * @param type The type to deep-copy.
   * @return Returns a new {@link DeepCopyMapperBuilder} for further configuration.
   */
  public static <T> DeepCopyMapperBuilder<T> of(Class<T> type) {
    return new DeepCopyMapperBuilder<>(type);
  }

  private static <T> Mapper<T, T> deepCopyMapperCached(Class<T> type, Map<Class<?>, TypeMapping<?, ?>> typeMappings,
      Map<Class<?>, Mapper<?, ?>> requiredMappers) {

    Mapping<T, T> mapping = Mapping.from(type)
        .to(type);

    Set<PropertyDescriptor> writableProperties = Properties.getProperties(type, Target.DESTINATION);
    writableProperties.stream()
        .forEach(pd -> {
          Class<?> propertyType = pd.getPropertyType();
          // Find all mappers for generic types if the property is a map or collection.
          if (isCollection(propertyType) || isMap(propertyType)) {
            GenericParameterContext paramCtx = new GenericParameterContext(pd.getReadMethod());
            findNestedGenericTypes(mapping, paramCtx, typeMappings, requiredMappers);
          } else {
            findMapper(mapping, propertyType, typeMappings, requiredMappers);
          }
        });
    requiredMappers.entrySet()
        .stream()
        .map(Entry::getValue)
        .forEach(mapper -> mapping.useMapper(mapper));
    return mapping.mapper();
  }

  private static <T> void findMapper(Mapping<T, T> mapping, Class<?> propertyType,
      Map<Class<?>, TypeMapping<?, ?>> typeMappings, Map<Class<?>, Mapper<?, ?>> requiredMappers) {
    // Use customized copy function in precedence
    if (typeMappings.containsKey(propertyType)) {
      TypeMapping<?, ?> typeMapping = typeMappings.get(propertyType);
      if (!mapping.hasMapperFor(propertyType, propertyType)) {
        mapping.useMapper(typeMapping);
      }
    }
    // Try to create identity mapper
    else if (!isReferenceMapping(propertyType)) {
      if (!requiredMappers.containsKey(propertyType)) {
        Mapper<?, ?> mapper = deepCopyMapperCached(propertyType, typeMappings, requiredMappers);
        requiredMappers.put(propertyType, mapper);
      }
    }
  }

  private static <T> void findNestedGenericTypes(Mapping<T, T> mapping, GenericParameterContext paramCtx,
      Map<Class<?>, TypeMapping<?, ?>> typeMappings, Map<Class<?>, Mapper<?, ?>> requiredMappers) {
    Class<?> currentType = paramCtx.getCurrentType();
    if (isCollection(currentType) || isMap(currentType)) {
      findNestedGenericTypes(mapping, paramCtx.goInto(0), typeMappings, requiredMappers);
      if (isMap(currentType)) {
        findNestedGenericTypes(mapping, paramCtx.goInto(1), typeMappings, requiredMappers);
      }
    } else {
      findMapper(mapping, currentType, typeMappings, requiredMappers);
    }
  }

  private static boolean isReferenceMapping(Class<?> propertyType) {
    return !ReflectionUtil.isBean(propertyType) || propertyType.isEnum() || ReflectionUtil.isPrimitive(propertyType)
        || ReflectionUtil.isBuildInType(propertyType);
  }

}
