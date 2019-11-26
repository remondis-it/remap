package com.remondis.remap;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

import java.beans.PropertyDescriptor;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class DeepCopyMapperUtil {

  private DeepCopyMapperUtil() {

  }

  public static class DeepCopyMapperBuilder<T> {
    private Class<T> type;
    private Map<Class<?>, TypeMapping<?, ?>> typeMappings;

    protected DeepCopyMapperBuilder(Class<T> type) {
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
    public <S> DeepCopyMapperBuilder<T> exclude(Class<S> typeToCopyByReference) {
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
     * @param nonBeanTypeMapping The type mapping to create a copy from a specific type.
     * @return Returns this {@link DeepCopyMapperBuilder} for further configuration.
     */
    public <S> DeepCopyMapperBuilder<T> withTypeMapping(TypeMapping<S, S> nonBeanTypeMapping) {
      requireNonNull(nonBeanTypeMapping, "type mapping must not be null!");
      typeMappings.put(nonBeanTypeMapping.getProjection()
          .getSource(), nonBeanTypeMapping);
      return this;
    }

    public Mapper<T, T> getMapper() {
      return deepCopyMapperCached(type, typeMappings, new Hashtable<>());
    }
  }

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
          if (typeMappings.containsKey(propertyType)) {
            TypeMapping<?, ?> typeMapping = typeMappings.get(propertyType);
            if (!mapping.hasMapperFor(propertyType, propertyType)) {
              mapping.useMapper(typeMapping);
            }
          } else if (!isReferenceMapping(pd.getPropertyType())) {
            if (!requiredMappers.containsKey(propertyType)) {
              Mapper<?, ?> mapper = deepCopyMapperCached(propertyType, typeMappings, requiredMappers);
              requiredMappers.put(propertyType, mapper);
            }
          }
        });
    requiredMappers.entrySet()
        .stream()
        .map(Entry::getValue)
        .forEach(mapper -> mapping.useMapper(mapper));
    return mapping.mapper();
  }

  private static boolean isReferenceMapping(Class<?> propertyType) {
    return !ReflectionUtil.isBean(propertyType) || propertyType.isEnum() || ReflectionUtil.isPrimitive(propertyType)
        || ReflectionUtil.isBuildInType(propertyType);
  }

}
