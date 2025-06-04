package com.remondis.remap;

import static com.remondis.remap.MappingConfiguration.getPropertyFromFieldSelector;
import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The mapping model provides access to field mappings. The field mappings can be used to transform single values.
 *
 * @param <S> The source type.
 * @param <D> The destination type.
 */
public class MappingModel<S, D> {

  private MappingConfiguration<S, D> mapping;

  MappingModel(MappingConfiguration<S, D> mapping) {
    this.mapping = mapping;
  }

  /**
   * @param sourcePropertySelector (Optional) The source selector predicate. May be <code>null</code> if the search is
   *        intended by the destination field.
   * @return Returns the search result.
   */
  public TransformationSearchResult findMappingBySource(Predicate<String> sourcePropertySelector) {
    return findMapping(sourcePropertySelector, null);
  }

  /**
   * @param destinationPropertySelector The destination selector predicate.
   * @return Returns the search result.
   */
  public TransformationSearchResult findMappingByDestination(Predicate<String> destinationPropertySelector) {
    List<Transformation> transformations = mapping.getMappings()
        .stream()
        .filter(andOnDemand(null, destinationPropertySelector))
        .collect(Collectors.toList());
    return new TransformationSearchResult(transformations);
  }

  /**
   * @param sourceSelector The destination field selector.
   * @return Returns the search result.
   */
  public TransformationSearchResult findMappingBySource(FieldSelector<S> sourceSelector) {
    return findMapping(sourceSelector, null);
  }

  /**
   * @param destinationSelector The destination field selector.
   * @return Returns the search result.
   */
  public TransformationSearchResult findMappingByDestination(FieldSelector<D> destinationSelector) {
    return findMapping(null, destinationSelector);
  }

  /**
   * @param sourceSelector The destination field selector.
   * @param destinationSelector The destination field selector.
   * @return Returns the search result.
   */
  public TransformationSearchResult findMapping(FieldSelector<S> sourceSelector, FieldSelector<D> destinationSelector) {
    PropertyDescriptor sourceProperty = null;
    PropertyDescriptor destinationProperty = null;

    if (nonNull(sourceSelector)) {
      sourceProperty = getPropertyFromFieldSelector(Target.SOURCE, "findMapping", mapping.getSource(), sourceSelector,
          mapping.isFluentSettersAllowed());
    }

    if (nonNull(destinationSelector)) {
      destinationProperty = getPropertyFromFieldSelector(Target.DESTINATION, "findMapping", mapping.getDestination(),
          destinationSelector, mapping.isFluentSettersAllowed());
    }

    Predicate<String> sourcePredicate = isNull(sourceProperty) ? null : nameEqualsPredicate(sourceProperty.getName());
    Predicate<String> destinationPredicate = isNull(destinationProperty) ? null
        : nameEqualsPredicate(destinationProperty.getName());

    return findMapping(sourcePredicate, destinationPredicate);
  }

  /**
   * @param sourcePropertySelector (Optional) The source selector predicate. May be <code>null</code> if the search is
   *        intended by the destination field.
   * @param destinationPropertySelector The destination selector predicate.
   * @return Returns the search result.
   */
  public TransformationSearchResult findMapping(Predicate<String> sourcePropertySelector,
      Predicate<String> destinationPropertySelector) {
    List<Transformation> transformations = mapping.getMappings()
        .stream()
        .filter(andOnDemand(sourcePropertySelector, destinationPropertySelector))
        .collect(Collectors.toList());
    MappingModel<S, D>.TransformationSearchResult candidates = new TransformationSearchResult(transformations);
    boolean allMatch = transformations.stream()
        .allMatch(t -> t instanceof OmitTransformation);

    if (allMatch) {
      return mapping.getMappings()
          .stream()
          .filter(t -> t instanceof RestructureTransformation)
          .map(t -> (RestructureTransformation) t)
          .map(rT -> rT.getRestructureMapper()
              .getMappingModel()
              .findMapping(sourcePropertySelector, destinationPropertySelector))
          .map(tsr -> tsr.getTransformations())
          .reduce((list1, list2) -> {
            list1.addAll(list2);
            return list1;
          })
          .map(TransformationSearchResult::new)
          .orElse(candidates);

    } else {
      return candidates;
    }
  }

  private Predicate<Transformation> andOnDemand(Predicate<String> sourcePropertySelector,
      Predicate<String> destinationPropertySelector) {
    Predicate<Transformation> sourcePredicate = isNull(sourcePropertySelector) ? null
        : toSourcePredicate(sourcePropertySelector);
    Predicate<Transformation> destPredicate = isNull(destinationPropertySelector) ? null
        : toDestPredicate(destinationPropertySelector);

    Optional<Predicate<Transformation>> reduce = asList(sourcePredicate, destPredicate).stream()
        .filter(Objects::nonNull)
        .reduce(Predicate::and);

    return reduce.orElse(p -> false);
  }

  /**
   * Returns a {@link Predicate} to select a field by field name. This predicate matches if the field name
   * {@link String#equals(Object)} the Java Bean property name.
   *
   * @param sourceFieldName The source field name.
   * @return Returns a new {@link Predicate}.
   */
  public static Predicate<String> nameEqualsPredicate(String sourceFieldName) {
    return s -> sourceFieldName.equals(s);
  }

  /**
   * Returns a {@link Predicate} to select a field by field name while ignoring the case. This predicate matches if the
   * field name {@link String#equalsIgnoreCase(String)} the Java Bean property name.
   *
   * @param sourceFieldName The source field name.
   * @return Returns a new {@link Predicate}.
   */
  public static Predicate<String> nameEqualsPredicateIgnoreCase(String sourceFieldName) {
    return s -> sourceFieldName.equalsIgnoreCase(s);
  }

  private Predicate<Transformation> toPredicate(Predicate<String> propertyNamePredicate,
      Function<Transformation, PropertyDescriptor> pdExtractor) {
    requireNonNull(propertyNamePredicate, "predicate must not be null!");
    return t -> {
      return propertyNamePredicate.test(pdExtractor.apply(t)
          .getName());
    };
  }

  private Predicate<Transformation> toSourcePredicate(Predicate<String> sourcePropertySelector) {
    Function<Transformation, PropertyDescriptor> pdExtractor = Transformation::getSourceProperty;
    return hasPropertyDescriptor(pdExtractor).and(toPredicate(sourcePropertySelector, pdExtractor));
  }

  private Predicate<Transformation> toDestPredicate(Predicate<String> destinationPropertySelector) {
    Function<Transformation, PropertyDescriptor> pdExtractor = Transformation::getDestinationProperty;
    return hasPropertyDescriptor(pdExtractor).and(toPredicate(destinationPropertySelector, pdExtractor));
  }

  private Predicate<Transformation> hasPropertyDescriptor(Function<Transformation, PropertyDescriptor> pdExtractor) {
    return t -> nonNull(pdExtractor.apply(t));
  }

  @Override
  public String toString() {
    return "MappingModel [mapping=" + mapping + "]";
  }

  /**
   * Represents the search results, when inspecting the {@link MappingModel}.
   */
  public class TransformationSearchResult implements Iterable<TransformationSearchResult> {

    private List<Transformation> transformations;

    TransformationSearchResult(List<Transformation> transformations) {
      super();
      this.transformations = transformations;
    }

    List<Transformation> getTransformations() {
      return transformations;
    }

    @Override
    public Iterator<TransformationSearchResult> iterator() {
      return transformations.stream()
          .map(transformation -> new TransformationSearchResult(asList(transformation)))
          .collect(toList())
          .iterator();
    }

    /**
     * @return Returns <code>true</code> if the search has a result, <code>false</code>
     *         otherwise.
     */
    public boolean hasResult() {
      return !transformations.isEmpty();
    }

    /**
     * @return Returns <code>true</code> if the search has exactly one result, <code>false</code>
     *         otherwise.
     */
    public boolean hasSingleResult() {
      return hasResult() && transformations.size() == 1;
    }

    /**
     * @return Returns <code>true</code> if the search has multiple results, <code>false</code>
     *         otherwise.
     */
    public boolean hasMultipleResults() {
      return hasResult() && transformations.size() > 1;
    }

    /**
     * Checks if the matching transformation is a value transformation. Note: There are also transformations that use
     * the
     * whole source object for mapping. In this case use {@link #isObjectTransformation()} to determine source object
     * transformations.
     * This method expects a single search result, use {@link #hasSingleResult()} before calling this method.
     *
     * @return Returns <code>true</code> if the transformation expects a single field value, otherwise
     *         <code>false</code>
     *         is returned.
     *
     */
    public boolean isValueTransformation() {
      return !(getSingleMatch() instanceof RestructureTransformation)
          && !(getSingleMatch() instanceof SetTransformation);
    }

    /**
     * Checks if the matching transformation is a source object transformation.
     * This method expects a single search result, use {@link #hasSingleResult()} before calling this method.
     *
     * @return Returns <code>true</code> if the transformation expects the whole source object for mapping, otherwise
     *         <code>false</code> is returned.
     *
     */
    public boolean isObjectTransformation() {
      return !isValueTransformation();
    }

    /**
     * Checks if this transformation is likely to delegate to nested mappers. <b>Note:</b> This method expects a single
     * search
     * result, use {@link #hasSingleResult()} before calling this method.
     *
     * @return Returns <code>true</code> if the transformation will delegate to nested mappers, otherwise
     *         <code>false</code> is returned.
     */
    public boolean isDelegatingTransformation() {
      return (getSingleMatch() instanceof ReassignTransformation);
    }

    /**
     * Checks if the mapping configuration has a mapper registered for the specified type mapping.
     *
     * @param sourceType The source type.
     * @param targetType The destination type.
     * @return Returns <code>true</code> if the mapping configuration has a mapper for the specified type mapping,
     *         otherwise <code>false</code> is returned.
     */
    public boolean hasMapperFor(Class<?> sourceType, Class<?> targetType) {
      try {
        getMapperFor(sourceType, targetType);
        return true;
      } catch (Exception e) {
        return false;
      }
    }

    /**
     * Returns a {@link Mapper} if the specified type mapping was registered on the mapper.
     *
     * @param sourceType The source type.
     * @param destinationType The destination type.
     * @return Returns a {@link Mapper} if registered.
     * @throws IllegalStateException If the mapper cannot be found. To avoid this, check with
     *         {@link #hasMapperFor(Class, Class)} before.
     */
    public Mapper<?, ?> getMapperFor(Class<?> sourceType, Class<?> destinationType) {
      InternalMapper<?, ?> internalMapper = getSingleMatch().getMapperFor(sourceType, destinationType);
      if (internalMapper instanceof MapperAdapter) {
        MapperAdapter mapperAdapter = (MapperAdapter) internalMapper;
        return mapperAdapter.getMapper();
      } else {
        throw new IllegalStateException(String.format("Could not find mapping for filter model. Mapping was %s to %s.",
            sourceType.getName(), destinationType.getName()));
      }
    }

    /**
     * Performs a value transformation. This method expects a single search result, use {@link #hasSingleResult()}
     * before calling this method.
     *
     * @param value The value to map. Use {@link #isObjectTransformation()} to determine which input value is to be
     *        used.
     * @return Returns the {@link MappedResult}.
     */
    public MappedResult performValueTransformation(Object value) {
      Transformation singleMatch = getSingleMatch();
      return singleMatch.performValueTransformation(value, null);
    }

    private Transformation getSingleMatch() {
      if (!hasSingleResult()) {
        throw new IllegalStateException(
            "A value transformation cannot be performed on the mapping search result, because the result is unambiguous. "
                + "Please use hasSingleResult() before calling this method.");
      }
      return transformations.get(0);
    }

    /**
     * @return Returns the source property name.
     */
    public String getSourcePropertyName() {
      return getSingleMatch().getSourcePropertyName();
    }

    /**
     * @return Returns the destination property name.
     */
    public String getDestinationPropertyName() {
      return getSingleMatch().getDestinationPropertyName();
    }

    /**
     * @return Returns the source property descriptor.
     */
    public PropertyDescriptor getSource() {
      return getSingleMatch().getSourceProperty();
    }

    /**
     * @return Returns the destination property descriptor.
     */
    public PropertyDescriptor getDestination() {
      return getSingleMatch().getDestinationProperty();
    }

    @Override
    public String toString() {
      return "TransformationSearchResult [hasResult()=" + hasResult() + ", hasSingleResult()=" + hasSingleResult()
          + ", transformations=" + transformations + "]";
    }

  }
}
