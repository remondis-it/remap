package com.remondis.remap;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

import java.beans.PropertyDescriptor;
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

  public MappingModel(MappingConfiguration<S, D> mapping) {
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
    return new TransformationSearchResult(transformations);
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

  public class TransformationSearchResult {

    private List<Transformation> transformations;

    TransformationSearchResult(List<Transformation> transformations) {
      super();
      this.transformations = transformations;
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
     * Checks if the matching transformation is a value transformation. Note: There are also transformation that use the
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
      return getSource().getName();
    }

    /**
     * @return Returns the destination property name.
     */
    public String getDestinationPropertyName() {
      return getDestination().getName();
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
