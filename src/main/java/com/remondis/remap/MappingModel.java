package com.remondis.remap;

import static java.util.Objects.requireNonNull;

import java.beans.PropertyDescriptor;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MappingModel<S, D> {

  private MappingConfiguration<S, D> mapping;

  public MappingModel(MappingConfiguration<S, D> mapping) {
    this.mapping = mapping;
  }

  public FieldTransformation getFieldTransformation(Predicate<String> sourcePropertySelector,
      Predicate<String> destinationPropertySelector) {
    requireNonNull(sourcePropertySelector, "sourcePropertySelector must not be null!");
    requireNonNull(destinationPropertySelector, "destinationPropertySelector must not be null!");
    List<Transformation> transformations = mapping.getMappings()
        .stream()
        .filter(toSourcePredicate(sourcePropertySelector).and(toDestPredicate(destinationPropertySelector)))
        .collect(Collectors.toList());
    denyMappingNotFound(transformations);
    denyMultipleMappingsFound(transformations);
    Transformation match = transformations.get(0);
    return FieldTransformation.of(match);
  }

  private void denyMappingNotFound(List<Transformation> collect) {
    if (collect.isEmpty()) {
      throw new MappingException("Could not find a field mapping matching the specified search criteria.");
    }
  }

  private void denyMultipleMappingsFound(List<Transformation> collect) {
    StringBuilder b = new StringBuilder();
    MappingConfiguration.transformationToString(collect, true, b);
    if (collect.size() > 1) {
      throw new MappingException("The selected search criteria returned more than one mapping:\n" + b.toString());
    }
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
    return toPredicate(sourcePropertySelector, Transformation::getSourceProperty);
  }

  private Predicate<Transformation> toDestPredicate(Predicate<String> destinationPropertySelector) {
    return toPredicate(destinationPropertySelector, Transformation::getDestinationProperty);
  }

}
