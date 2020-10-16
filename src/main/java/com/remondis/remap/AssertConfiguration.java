package com.remondis.remap;

import com.googlecode.openbeans.PropertyDescriptor;

import static com.remondis.remap.Lang.denyNull;
import static com.remondis.remap.MappingConfiguration.OMIT_FIELD_DEST;
import static com.remondis.remap.MappingConfiguration.OMIT_FIELD_SOURCE;
import static com.remondis.remap.MappingConfiguration.getPropertyFromFieldSelector;
import static com.remondis.remap.MappingConfiguration.getTypedPropertyFromFieldSelector;
import static com.remondis.remap.OmitTransformation.omitDestination;
import static com.remondis.remap.OmitTransformation.omitSource;
import static com.remondis.remap.ReassignBuilder.ASSIGN;
import static com.remondis.remap.ReplaceBuilder.TRANSFORM;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class AssertConfiguration<S, D> {

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

  /**
   * Flag indicating that mappings that are not expected by asserts must be omitInSource mappings.
   */
  private boolean omitOthersSource = false;
  /**
   * Flag indicating that mappings that are not expected by asserts must be omitInDestination mappings.
   */
  private boolean omitOthersDestination = false;

  private Object expectN;

  private boolean expectNoImplicitMappings;
  private List<AssertVerification> verificaions;

  AssertConfiguration(Mapper<S, D> mapper) {
    denyNull("mapper", mapper);
    this.mapper = mapper;
    this.assertedTransformations = new HashSet<>();
    this.verificaions = new LinkedList<>();
  }

  /**
   * Specifies an assertion for a reassign operation.
   *
   * @param sourceSelector
   *        The source field selector.
   * @return Returns a {@link ReassignAssertBuilder} for further configuration.
   */
  public <RS> ReassignAssertBuilder<S, D, RS> expectReassign(FieldSelector<S> sourceSelector) {
    denyNull("sourceSelector", sourceSelector);
    PropertyDescriptor sourceProperty = getPropertyFromFieldSelector(Target.SOURCE, ASSIGN, getMapping().getSource(),
        sourceSelector);
    ReassignAssertBuilder<S, D, RS> reassignBuilder = new ReassignAssertBuilder<S, D, RS>(sourceProperty,
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
   * @return Returns a {@link SetAssertBuilder} for further configuration.
   */
  public <RD> SetAssertBuilder<S, D, RD> expectSet(TypedSelector<RD, D> destinationSelector) {
    denyNull("destinationSelector", destinationSelector);

    TypedPropertyDescriptor<RD> destProperty = getTypedPropertyFromFieldSelector(Target.DESTINATION, TRANSFORM,
        getMapping().getDestination(), destinationSelector);
    SetAssertBuilder<S, D, RD> builder = new SetAssertBuilder<>(destProperty, this);
    return builder;
  }

  /**
   * Specifies an assertion for a restructure operation.
   *
   * @param destinationSelector
   *        The destination field selector.
   * @return Returns a {@link RestructureAssertBuilder} for further configuration.
   */
  public <RD> RestructureAssertBuilder<S, D, RD> expectRestructure(TypedSelector<RD, D> destinationSelector) {
    denyNull("destinationSelector", destinationSelector);

    TypedPropertyDescriptor<RD> destProperty = getTypedPropertyFromFieldSelector(Target.DESTINATION, TRANSFORM,
        getMapping().getDestination(), destinationSelector);
    RestructureAssertBuilder<S, D, RD> builder = new RestructureAssertBuilder<>(destProperty, this);
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
   * @return Returns a {@link AssertConfiguration} for further configuration.
   */
  public AssertConfiguration<S, D> expectOmitInSource(FieldSelector<S> sourceSelector) {
    denyNull("sourceSelector", sourceSelector);
    // Omit in destination
    PropertyDescriptor propertyDescriptor = getPropertyFromFieldSelector(Target.SOURCE, OMIT_FIELD_SOURCE,
        getMapping().getSource(), sourceSelector);
    OmitTransformation omitSource = omitSource(getMapping(), propertyDescriptor);
    _add(omitSource);
    return this;
  }

  /**
   * Expects the mapper to suppress creation of implicit mappings. Note: This requires the user to define the mappings
   * explicitly using {@link MappingConfiguration#reassign(FieldSelector)} or any other mapping operation. Therefore all
   * this
   * explicit
   * mappings must be backed by an assertion.
   *
   * @return Returns this instance for further configuration.
   */
  public AssertConfiguration<S, D> expectNoImplicitMappings() {
    this.expectNoImplicitMappings = true;
    return this;
  }

  /**
   * Specifies an assertion for a destination field to be omitted.
   *
   * @param destinationSelector
   *        The destination field selector.
   * @return Returns a {@link AssertConfiguration} for further configuration.
   */
  public AssertConfiguration<S, D> expectOmitInDestination(FieldSelector<D> destinationSelector) {
    denyNull("destinationSelector", destinationSelector);
    PropertyDescriptor propertyDescriptor = getPropertyFromFieldSelector(Target.DESTINATION, OMIT_FIELD_DEST,
        getMapping().getDestination(), destinationSelector);
    OmitTransformation omitDestination = omitDestination(getMapping(), propertyDescriptor);
    _add(omitDestination);
    return this;
  }

  /**
   * Expects all other field to be omitted.
   *
   * @return Returns a {@link AssertConfiguration} for further configuration.
   */
  public AssertConfiguration<S, D> expectOthersToBeOmitted() {
    expectOtherSourceFieldsToBeOmitted();
    expectOtherDestinationFieldsToBeOmitted();
    return this;
  }

  /**
   * Expects all other source fields to be omitted.
   *
   * @return Returns a {@link AssertConfiguration} for further configuration.
   */
  public AssertConfiguration<S, D> expectOtherSourceFieldsToBeOmitted() {
    this.omitOthersSource = true;
    return this;
  }

  /**
   * Expects all other destination fields to be omitted.
   *
   * @return Returns a {@link AssertConfiguration} for further configuration.
   */
  public AssertConfiguration<S, D> expectOtherDestinationFieldsToBeOmitted() {
    this.omitOthersDestination = true;
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
    checkImplicitMappingStrategy();
    checkReplaceTransformations();

    checkVerifications();
    checkTransformations();
    checkReplaceFunctions();
  }

  private void checkVerifications() {
    verificaions.stream()
        .forEach(AssertVerification::verify);
  }

  private void checkImplicitMappingStrategy() {
    if (!mapper.getMapping()
        .isNoImplicitMappings() && expectNoImplicitMappings) {
      throw new AssertionError("The mapper was expected to create no implicit mappings but the actual mapper does.");
    } else if (mapper.getMapping()
        .isNoImplicitMappings() && !expectNoImplicitMappings) {
      throw new AssertionError("The mapper was expected to create implicit mappings but the actual mapper does not.");
    }
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
  private static <S, D> void assertionErrorIfNullCheckFails(Transformation r, Function<S, D> transformation)
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
              throw new AssertionError(DIFFERENT_NULL_STRATEGY + replace.toString());
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
      Stream<Transformation> tranformationStream = mappings.stream();

      // If omit others for destination, then all omitInDestination transformations are expected.
      if (omitOthersDestination) {
        tranformationStream = tranformationStream.filter(t -> {
          if (t instanceof OmitTransformation) {
            OmitTransformation omitTransformation = (OmitTransformation) t;
            return !(omitTransformation.isOmitInDestination());
          } else {
            return true;
          }
        });
      }
      // If omit others for source, then all omitInSource transformations are expected.
      if (omitOthersSource) {
        tranformationStream = tranformationStream.filter(t -> {
          if (t instanceof OmitTransformation) {
            OmitTransformation omitTransformation = (OmitTransformation) t;
            return !(omitTransformation.isOmitInSource());
          } else {
            return true;
          }
        });
      }

      // Ignore MapTransformations, because those are implicit transformations.
      tranformationStream = tranformationStream.filter(t -> {
        return !(t instanceof MapTransformation);
      });

      // All remaining transformations were not backed by an assert.
      Set<Transformation> unexpectedTransformations = tranformationStream.collect(Collectors.toSet());
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

  MappingConfiguration<S, D> getMapping() {
    return mapper.getMapping();
  }

  /**
   * Method to add custom verifications, that cannot be performed by a comparision of {@link Transformation} using
   * equals.
   */
  void addVerification(AssertVerification verification) {
    denyNull("verification", verification);
    this.verificaions.add(verification);
  }
}
