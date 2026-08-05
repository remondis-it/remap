package com.remondis.remap;

import static com.remondis.remap.Properties.asString;
import static com.remondis.remap.ReflectionUtil.getCollector;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * The reassign operation maps a field to another field while the field names may differ. A reassign operation is only
 * allowed on fields of the same type.
 *
 * @author schuettec
 */
public class ReassignTransformation extends Transformation {

  private static final String REASSIGNING_MSG = "Reassigning %s\n           to %s";

  /*
   * The generic parameter contexts describe the static generic type structure of the source and destination property
   * and are therefore resolved once at configuration time instead of for every mapping operation.
   * GenericParameterContext is effectively immutable after construction - goInto() returns new instances - so the
   * cached contexts can be shared by concurrent mapping operations.
   */
  private final GenericParameterContext sourceContext;
  private final GenericParameterContext destinationContext;

  /**
   * The conversion strategy of this transformation, resolved by {@link #validateTransformation()}. Mapper registry
   * lookups and the collection/map/value dispatch are static configuration knowledge, so they are resolved once at
   * validation time instead of for every mapped value. Volatile for safe publication when the mapper is shared
   * between threads.
   */
  private volatile ConversionStrategy conversionStrategy;

  /**
   * A single conversion step of this transformation, resolved once at validation time.
   */
  @FunctionalInterface
  private interface ConversionStrategy {
    Object convert(Object sourceValue, Object destination);
  }

  ReassignTransformation(MappingConfiguration<?, ?> mapping, PropertyDescriptor sourceProperty,
      PropertyDescriptor destinationProperty) {
    super(mapping, sourceProperty, destinationProperty);
    this.sourceContext = new GenericParameterContext(sourceProperty.getReadMethod());
    this.destinationContext = new GenericParameterContext(destinationProperty.getReadMethod());
  }

  protected static boolean isEqualTypes(Class<?> sourceType, Class<?> destinationType) {
    return sourceType.equals(destinationType);
  }

  private boolean isReferenceMapping(Class<?> sourceType, Class<?> destinationType) {
    return isEqualTypes(sourceType, destinationType) || ReflectionUtil.isWrapper(sourceType, destinationType)
        || ReflectionUtil.isWrapper(destinationType, sourceType);
  }

  @Override
  protected void performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty, Object destination) throws MappingException {
    Object sourceValue = readOrFail(sourceProperty, source);
    MappedResult result = MappedResult.skip();

    if (sourceValue != null) {
      result = performValueTransformation(sourceValue, destination);
    }

    if (result.hasValue() || mapping.isWriteNull()) {
      writeOrFail(destinationProperty, destination, result.getValue());
    }
  }

  @Override
  protected MappedResult performValueTransformation(Object source, Object destination) throws MappingException {
    return MappedResult.value(conversionStrategy.convert(source, destination));
  }

  /**
   * Resolves the conversion strategy for the current generic type level. The resolution mirrors the former dispatch
   * per mapped value: a registered mapper takes precedence, maps and collections are rebuilt per entry/element, all
   * remaining types are reference mappings - the validation guarantees that any other type conversion has a
   * registered mapper.
   */
  @SuppressWarnings({
      "unchecked", "rawtypes"
  })
  private ConversionStrategy buildConversionStrategy(GenericParameterContext sourceCtx,
      GenericParameterContext destCtx) {
    Class<?> sourceType = sourceCtx.getCurrentType();
    Class<?> destinationType = destCtx.getCurrentType();
    InternalMapper mapper = getMapperForOrNull(sourceType, destinationType);
    if (mapper != null) {
      return (sourceValue, destination) -> {
        if (destination != null) {
          Object destPropertyValue = readOrFail(destinationProperty, destination);
          return mapper.map(sourceValue, destPropertyValue);
        }
        return mapper.map(sourceValue, null);
      };
    } else if (isMap(sourceType)) {
      ConversionStrategy keyStrategy = buildConversionStrategy(sourceCtx.goInto(0), destCtx.goInto(0));
      ConversionStrategy valueStrategy = buildConversionStrategy(sourceCtx.goInto(1), destCtx.goInto(1));
      return mapStrategy(keyStrategy, valueStrategy);
    } else if (isCollection(sourceType)) {
      GenericParameterContext sourceElementCtx = sourceCtx.goInto(0);
      GenericParameterContext destinationElementCtx = destCtx.goInto(0);
      ConversionStrategy elementStrategy = buildConversionStrategy(sourceElementCtx, destinationElementCtx);
      Collector collector = getCollector(destinationType);
      return collectionStrategy(sourceElementCtx.getCurrentType(), destinationElementCtx.getCurrentType(),
          elementStrategy, collector);
    } else {
      return (sourceValue, destination) -> sourceValue;
    }
  }

  @SuppressWarnings({
      "unchecked", "rawtypes"
  })
  private ConversionStrategy collectionStrategy(Class<?> sourceElementType, Class<?> destinationElementType,
      ConversionStrategy elementStrategy, Collector collector) {
    /*
     * Key-based matching for mapInto operations: the key extractors and the element mapper are static configuration
     * knowledge and are therefore resolved once at strategy build time. The destination element lookup depends on the
     * content of the current destination collection and has to be built per mapping operation.
     */
    CollectionMappingKey<Object, Object, ?> keyMapping = (CollectionMappingKey) getCollectionKeyMapping(
        sourceElementType, destinationElementType).orElse(null);
    InternalMapper elementMapper = keyMapping == null ? null
        : getMapperForOrNull(sourceElementType, destinationElementType);

    return (sourceValue, destination) -> {
      Collection collection = (Collection) sourceValue;

      Map<Object, Object> destLookup = null;
      if (destination != null && keyMapping != null && elementMapper != null) {
        Object destCollectionValue = readOrFail(destinationProperty, destination);
        if (destCollectionValue instanceof Collection) {
          destLookup = buildLookup((Collection<?>) destCollectionValue, keyMapping.getDestinationKeyExtractor());
        }
      }
      final Map<Object, Object> finalDestLookup = destLookup;

      return collection.stream()
          .map(element -> {
            if (element == null) {
              throw MappingException.nullElementInCollection(sourceProperty, destinationProperty);
            }
            // Try to find a matched destination element via key
            if (finalDestLookup != null) {
              Object key = keyMapping.getSourceKeyExtractor()
                  .apply(element);
              Object matchedDest = finalDestLookup.get(key);
              return elementMapper.map(element, matchedDest);
            }
            return elementStrategy.convert(element, null);
          })
          .collect(collector);
    };
  }

  private Map<Object, Object> buildLookup(Collection<?> collection, Function<Object, ?> keyExtractor) {
    Map<Object, Object> lookup = new LinkedHashMap<>();
    for (Object element : collection) {
      if (element != null) {
        Object key = keyExtractor.apply(element);
        if (key != null) {
          lookup.putIfAbsent(key, element);
        }
      }
    }
    return lookup;
  }

  @SuppressWarnings({
      "unchecked", "rawtypes"
  })
  private static ConversionStrategy mapStrategy(ConversionStrategy keyStrategy, ConversionStrategy valueStrategy) {
    return (sourceValue, destination) -> {
      Map<?, ?> map = Map.class.cast(sourceValue);
      return map.entrySet()
          .stream()
          .map(entry -> new AbstractMap.SimpleEntry(keyStrategy.convert(entry.getKey(), null),
              valueStrategy.convert(entry.getValue(), null)))
          .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    };
  }

  /**
   * Finds the generic return type of a method in nested generics. For example this method returns {@link String} when
   * called on a method like <code>List&lt;List&lt;Set&lt;String&gt;&gt;&gt; get();</code>.
   *
   * @param method The method to analyze.
   * @return Returns the inner generic type.
   */
  static Class<?> findGenericTypeFromMethod(Method method, int genericParameterIndex) {
    ParameterizedType parameterizedType = (ParameterizedType) method.getGenericReturnType();
    Type type = null;
    while (parameterizedType != null) {
      type = parameterizedType.getActualTypeArguments()[genericParameterIndex];
      if (type instanceof ParameterizedType) {
        parameterizedType = (ParameterizedType) type;
      } else if (type instanceof TypeVariable) {
        type = Object.class;
        parameterizedType = null;
      } else {
        parameterizedType = null;
      }
    }
    return (Class<?>) type;
  }

  static boolean isCollection(Class<?> type) {
    return Collection.class.isAssignableFrom(type);
  }

  @Override
  protected void validateTransformation() throws MappingException {
    // we have to check that all required mappers are known for nested mapping
    // if this transformation performs an object mapping, check for known mappers
    _validateTransformation(sourceContext, destinationContext);
    // All required mappers are available - bind the conversion strategy for the mapping hot path.
    this.conversionStrategy = buildConversionStrategy(sourceContext, destinationContext);
  }

  private void _validateTransformation(GenericParameterContext sourceCtx, GenericParameterContext destCtx) {
    // Travers nested types here and check for equal map/collection and existing type mapping.
    Class<?> sourceType = sourceCtx.getCurrentType();
    Class<?> destinationType = destCtx.getCurrentType();
    boolean incompatibleCollecion = (isMap(sourceType) && isCollection(destinationType))
        || (isCollection(sourceType) && isMap(destinationType))
        || (noCollectionOrMap(sourceType) && isCollectionOrMap(destinationType))
        || (isCollectionOrMap(sourceType) && noCollectionOrMap(destinationType));

    if (incompatibleCollecion) {
      throw MappingException.incompatibleCollectionMapping(getSourceProperty(), sourceCtx, getDestinationProperty(),
          destCtx);
    }
    if (isMap(sourceType)) {
      GenericParameterContext sourceKeyContext = sourceCtx.goInto(0);
      GenericParameterContext destKeyContext = destCtx.goInto(0);

      GenericParameterContext sourceValueContext = sourceCtx.goInto(1);
      GenericParameterContext destValueContext = destCtx.goInto(1);

      _validateTransformation(sourceKeyContext, destKeyContext);
      _validateTransformation(sourceValueContext, destValueContext);
    }
    if (isCollection(sourceType)) {
      GenericParameterContext sourceElemType = sourceCtx.goInto(0);
      GenericParameterContext destElemType = destCtx.goInto(0);
      _validateTransformation(sourceElemType, destElemType);
    } else {
      validateTypeMapping(getSourceProperty(), sourceType, getDestinationProperty(), destinationType);
    }
  }

  private static boolean noCollectionOrMap(Class<?> type) {
    return !isMap(type) && !isCollection(type);
  }

  private static boolean isCollectionOrMap(Class<?> type) {
    return !noCollectionOrMap(type);
  }

  private void validateTypeMapping(PropertyDescriptor sourceProperty, Class<?> sourceType,
      PropertyDescriptor destinationProperty, Class<?> destinationType) {

    if (!isReferenceMapping(sourceType, destinationType)) {
      // Check if there is a registered mapper if required.
      getMapperFor(sourceType, destinationType);
    }
  }

  private static boolean isMap(Class<?> sourceType) {
    return Map.class.isAssignableFrom(sourceType);
  }

  @Override
  public String toString(boolean detailed) {
    return String.format(REASSIGNING_MSG, asString(sourceProperty, detailed), asString(destinationProperty, detailed));

  }

}
