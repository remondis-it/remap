package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;
import static com.remondis.remap.Mapping.OMIT_FIELD_DEST;
import static com.remondis.remap.Mapping.OMIT_FIELD_SOURCE;
import static com.remondis.remap.Mapping.getPropertyFromFieldSelector;
import static com.remondis.remap.Mapping.getTypedPropertyFromFieldSelector;
import static com.remondis.remap.OmitTransformation.omitDestination;
import static com.remondis.remap.OmitTransformation.omitSource;
import static com.remondis.remap.ReassignBuilder.ASSIGN;
import static com.remondis.remap.ReplaceBuilder.TRANSFORM;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Creates a test for a {@link Mapper} object to assert the mapping specification. The expected mapping is to be
 * configured on this object. The method {@link #ensure()} then performs the assertions against the actual configured
 * mapping configuration of the specified mapper and performs checks using the specified transformation functions.
 * Transformation functions specified for the `replace` operation are checked against <code>null</code> and sample
 * values. It is expected that those test invocations do not throw an exception.
 *
 * @param <S>
 *        The type of the source objects
 * @param <D>
 *        The type of the destination objects.
 *
 * @author schuettec
 */
public class AssertMapping<S, D> {

  static final String DIFFERENT_NULL_STRATEGY = "The replace transformation specified by the mapper has a different "
      + "null value strategy than the expected transformation:\n";

  static final String UNEXPECTED_EXCEPTION = "Function threw an unexpected exception for transformation:\n";

  static final String NOT_NULL_SAFE = "The specified transformation function is not null-safe for operation:\n";

  static final String UNEXPECTED_TRANSFORMATION = "The following unexpected transformation "
      + "were specified on the mapping:\n";

  static final String EXPECTED_TRANSFORMATION = "The following expected transformation "
      + "were not specified on the mapping:\n";

  static final String TRANSFORMATION_ALREADY_ADDED = "The specified transformation was already added as an assertion";

  private Mapper<S, D> mapper;

  private Set<Transformation> assertedTransformations;

  private AssertMapping(Mapper<S, D> mapper) {
    denyNull("mapper", mapper);
    this.mapper = mapper;
    this.assertedTransformations = new HashSet<>();
  }

  /**
   * Creates a new specification of assertions that are to be checked for the specified mapper instance.
   *
   * @param mapper
   *        The {@link Mapper} instance.
   * @return Returns a new {@link AssertMapping} for method changing.
   */
  public static <S, D> AssertMapping<S, D> of(Mapper<S, D> mapper) {
    return new AssertMapping<S, D>(mapper);
  }

  /**
   * Specifies an assertion for a reassing operation.
   *
   * @param sourceSelector
   *        The source field selector.
   * @return Returns a {@link ReassignAssertBuilder} for further configuration.
   */
  public <RS> ReassignAssertBuilder<S, D, RS> expectReassign(TypedSelector<RS, S> sourceSelector) {
    denyNull("sourceSelector", sourceSelector);
    TypedPropertyDescriptor<RS> typedSourceProperty = getTypedPropertyFromFieldSelector(Target.SOURCE, ASSIGN,
        getMapping().getSource(), sourceSelector);
    ReassignAssertBuilder<S, D, RS> reassignBuilder = new ReassignAssertBuilder<S, D, RS>(typedSourceProperty,
        getMapping().getDestination(), this);
    return reassignBuilder;
  }

  /**
   * Specifies an assertion for a replace operation.
   *
   * @param sourceSelector
   *        The source field selector.
   * @param destinationSelector
   *        The destination field selector.
   * @return Returns a {@link ReplaceAssertBuilder} for further configuration.
   */
  public <RD, RS> ReplaceAssertBuilder<S, D, RD, RS> expectReplace(TypedSelector<RS, S> sourceSelector,
      TypedSelector<RD, D> destinationSelector) {
    denyNull("sourceSelector", sourceSelector);
    denyNull("destinationSelector", destinationSelector);

    TypedPropertyDescriptor<RS> sourceProperty = getTypedPropertyFromFieldSelector(Target.SOURCE, TRANSFORM,
        getMapping().getSource(), sourceSelector);
    TypedPropertyDescriptor<RD> destProperty = getTypedPropertyFromFieldSelector(Target.DESTINATION, TRANSFORM,
        getMapping().getDestination(), destinationSelector);

    ReplaceAssertBuilder<S, D, RD, RS> builder = new ReplaceAssertBuilder<>(sourceProperty, destProperty, this);
    return builder;
  }

  /**
   * Specifies an assertion for a set operation.
   *
   * @param destinationSelector
   *        The destination field selector.
   * @return Returns a {@link ReplaceAssertBuilder} for further configuration.
   */
  public <RD> SetAssertBuilder<S, D, RD> expectSet(TypedSelector<RD, D> destinationSelector) {
    denyNull("destinationSelector", destinationSelector);

    TypedPropertyDescriptor<RD> destProperty = getTypedPropertyFromFieldSelector(Target.DESTINATION, TRANSFORM,
        getMapping().getDestination(), destinationSelector);
    SetAssertBuilder<S, D, RD> builder = new SetAssertBuilder<>(destProperty, this);
    return builder;
  }

  /**
   * Specifies an assertion for a replace operation for collections.
   *
   * @param sourceSelector
   *        The source field selector.
   * @param destinationSelector
   *        The destination field selector.
   * @return Returns a {@link ReplaceCollectionAssertBuilder} for further configuration.
   */
  public <RD, RS> ReplaceCollectionAssertBuilder<S, D, RD, RS> expectReplaceCollection(
      TypedSelector<Collection<RS>, S> sourceSelector, TypedSelector<Collection<RD>, D> destinationSelector) {
    denyNull("sourceSelector", sourceSelector);
    denyNull("destinationSelector", destinationSelector);
    TypedPropertyDescriptor<Collection<RS>> sourceProperty = getTypedPropertyFromFieldSelector(Target.SOURCE,
        ReplaceBuilder.TRANSFORM, getMapping().getSource(), sourceSelector);
    TypedPropertyDescriptor<Collection<RD>> destProperty = getTypedPropertyFromFieldSelector(Target.DESTINATION,
        ReplaceBuilder.TRANSFORM, getMapping().getDestination(), destinationSelector);

    ReplaceCollectionAssertBuilder<S, D, RD, RS> builder = new ReplaceCollectionAssertBuilder<>(sourceProperty,
        destProperty, this);
    return builder;
  }

  private void _add(Transformation transformation) {
    if (assertedTransformations.contains(transformation)) {
      throw new AssertionError(TRANSFORMATION_ALREADY_ADDED);
    }
    assertedTransformations.add(transformation);
  }

  /**
   * Specifies an assertion for a source field to be omitted.
   *
   * @param sourceSelector
   *        The source field selector.
   * @return Returns a {@link AssertMapping} for further configuration.
   */
  public AssertMapping<S, D> expectOmitInSource(FieldSelector<S> sourceSelector) {
    denyNull("sourceSelector", sourceSelector);
    // Omit in destination
    PropertyDescriptor propertyDescriptor = getPropertyFromFieldSelector(Target.SOURCE, OMIT_FIELD_SOURCE,
        getMapping().getSource(), sourceSelector);
    OmitTransformation omitSource = omitSource(getMapping(), propertyDescriptor);
    _add(omitSource);
    return this;
  }

  /**
   * Specifies an assertion for a destination field to be omitted.
   *
   * @param destinationSelector
   *        The destination field selector.
   * @return Returns a {@link AssertMapping} for further configuration.
   */
  public AssertMapping<S, D> expectOmitInDestination(FieldSelector<D> destinationSelector) {
    denyNull("destinationSelector", destinationSelector);
    PropertyDescriptor propertyDescriptor = getPropertyFromFieldSelector(Target.DESTINATION, OMIT_FIELD_DEST,
        getMapping().getDestination(), destinationSelector);
    OmitTransformation omitDestination = omitDestination(getMapping(), propertyDescriptor);
    _add(omitDestination);
    return this;
  }

  /**
   * Performs the specified assertions against the specified mapper instance. If a replace operation was specified with
   * a transformation function to be also called for <code>null</code> values a null check is performed against the
   * function.
   *
   * @throws AssertionError
   *         Thrown if an assertion made about the {@link Mapper} object failed.
   */
  public void ensure() throws AssertionError {
    checkReplaceTransformations();
    checkTransformations();
    checkReplaceFunctions();
  }

  /**
   * This method checks the replace functions is null-safe if null strategy is not skip-when-null.
   */
  @SuppressWarnings("rawtypes")
  private void checkReplaceFunctions() {
    Set<Transformation> mappings = mapper.getMapping()
        .getMappings();
    mappings.stream()
        .filter(t -> {
          return (t instanceof SkipWhenNullTransformation);
        })
        .map(t -> {
          return (SkipWhenNullTransformation) t;
        })
        .forEach(r -> {
          Function<?, ?> transformation = r.getTransformation();
          if (!r.isSkipWhenNull()) {
            assertionErrorIfNullCheckFails(r, transformation);
          }
        });
  }

  /**
   * Throws an {@link AssertionError} if the specified {@link Function} is not null safe.
   *
   * @param r The {@link Transformation} that is validated.
   * @param transformation The {@link Function} that is null checked.
   * @throws AssertionError Thrown if the null check fails.
   */
  public static <S, D> void assertionErrorIfNullCheckFails(Transformation r, Function<S, D> transformation)
      throws AssertionError {
    try {
      transformation.apply(null);
    } catch (NullPointerException t) {
      throw new AssertionError(NOT_NULL_SAFE + r.toString(), t);
    } catch (Throwable t) {
      throw new AssertionError(UNEXPECTED_EXCEPTION + r.toString(), t);
    }
  }

  /**
   * This method checks that the expected replace transformations and the actual replace transformations have equal null
   * strategies.
   */
  @SuppressWarnings("rawtypes")
  private void checkReplaceTransformations() {
    Set<Transformation> mappings = mapper.getMapping()
        .getMappings();

    mappings.stream()
        .filter(t -> {
          return (t instanceof SkipWhenNullTransformation);
        })
        .map(t -> {
          return (SkipWhenNullTransformation) t;
        })
        .forEach(replace -> {
          Optional<SkipWhenNullTransformation> sameTransformation = assertedTransformations().stream()
              .filter(t -> {
                return (t instanceof SkipWhenNullTransformation);
              })
              .map(t -> {
                return (SkipWhenNullTransformation) t;
              })
              .filter(r -> {
                return r.getSourceProperty()
                    .equals(replace.getSourceProperty());
              })
              .filter(r -> {
                return r.getDestinationProperty()
                    .equals(replace.getDestinationProperty());
              })
              .findFirst();
          if (sameTransformation.isPresent()) {
            SkipWhenNullTransformation assertedReplaceTransformation = sameTransformation.get();
            // Check if the configured replace transformation has the same skip-null configuration than the asserted
            // one and throw if not
            if (replace.isSkipWhenNull() != assertedReplaceTransformation.isSkipWhenNull()) {
              throw new AssertionError(
                  DIFFERENT_NULL_STRATEGY + replace.toString() + "\n" + assertedTransformations.toString());
            }
          }
        });
  }

  private void checkTransformations() {
    Set<Transformation> mappings = getMapping().getMappings();
    Set<Transformation> assertedTransformations = assertedTransformations();

    // we have to check that the mapping list contains all asserted transformations
    mappings.removeAll(assertedTransformations);
    assertedTransformations.removeAll(getMapping().getMappings());

    if (!assertedTransformations.isEmpty()) {
      throw new AssertionError(EXPECTED_TRANSFORMATION + listCollection(assertedTransformations));
    }

    if (!mappings.isEmpty()) {
      // if there are more elements left, the remaining transformations must be MapTransformations
      Set<Transformation> unexpectedTransformations = mappings.stream()
          .filter(t -> {
            return !(t instanceof MapTransformation);
          })
          .collect(Collectors.toSet());
      if (!unexpectedTransformations.isEmpty()) {
        throw new AssertionError(UNEXPECTED_TRANSFORMATION + listCollection(unexpectedTransformations));
      }
    }
  }

  private String listCollection(Set<Transformation> transformations) {
    StringBuilder b = new StringBuilder();
    transformations.stream()
        .forEach(t -> {
          b.append("- " + t.toString())
              .append("\n");
        });
    return b.toString();
  }

  private Set<Transformation> assertedTransformations() {
    return new HashSet<>(assertedTransformations);
  }

  void addAssertion(Transformation transformation) {
    _add(transformation);
  }

  Mapping<S, D> getMapping() {
    return mapper.getMapping();
  }

}
