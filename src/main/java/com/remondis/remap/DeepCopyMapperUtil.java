package com.remondis.remap;

import java.beans.PropertyDescriptor;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class DeepCopyMapperUtil {

  private DeepCopyMapperUtil() {

  }

  public <T> Mapper<T, T> deepCopyMapper(Class<T> type) {
    return deepCopyMapperCached(type, new Hashtable<>());
  }

  private <T> Mapper<T, T> deepCopyMapperCached(Class<T> type, Map<Class<?>, Mapper<?, ?>> requiredMappers) {
    Set<PropertyDescriptor> writableProperties = Properties.getProperties(type, Target.DESTINATION);
    writableProperties.stream()
        .filter(pd -> !ReassignTransformation.isReferenceMapping(pd.getPropertyType(), pd.getPropertyType()))
        .forEach(pd -> {
          Class<?> propertyType = pd.getPropertyType();
          if (!requiredMappers.containsKey(propertyType)) {
            Mapper<?, ?> mapper = deepCopyMapperCached(propertyType, requiredMappers);
            requiredMappers.put(propertyType, mapper);
          }
        });
    Mapping<T, T> mapping = Mapping.from(type)
        .to(type);
    requiredMappers.entrySet()
        .stream()
        .map(Entry::getValue)
        .forEach(mapper -> mapping.useMapper(mapper));
    return mapping.mapper();
  }

}
