package com.remondis.remap;

import static com.remondis.remap.Mapping.OMIT_FIELD_DEST;
import static com.remondis.remap.Mapping.OMIT_FIELD_SOURCE;
import static com.remondis.remap.Mapping.getPropertyFromFieldSelector;
import static com.remondis.remap.Mapping.getTypedPropertyFromFieldSelector;
import static com.remondis.remap.OmitTransformation.omitDestination;
import static com.remondis.remap.OmitTransformation.omitSource;
import static com.remondis.remap.ReassignBuilder.ASSIGN;
import static com.remondis.remap.ReplaceBuilder.TRANSFORM;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Creates a test for a {@link Mapper} object to assert the mapping specification. The expected mapping is to be
 * configured on this object. The method {@link #ensure()} then performs the assertions against the actual configured
 * mapping configuration of the specified mapper and performs checks using the specified transformation functions.
 * Transformation functions specified for the `replace` operation are checked against <code>null</code> and sample
 * values. It is expected that those test invocations do not throw an exception.
 * 
 * @param <S>
 *          The type of the source objects
 * @param <D>
 *          The type of the destination objects.
 *
 * @author schuettec
 */
public class AssertMapping<S, D> {

  static final String TRANSFORMATION_ALREADY_ADDED = "The specified transformation was already added as an assertion";

  private Mapper<S, D> mapper;

  private Set<Transformation> assertedTransformations;

  public AssertMapping(Mapper<S, D> mapper) {
    this.mapper = mapper;
    this.assertedTransformations = new HashSet<>();
  }

  public static <S, D> AssertMapping<S, D> of(Mapper<S, D> mapper) {
    return new AssertMapping<S, D>(mapper);
  }

  public <RS> ReassignAssertBuilder<S, D, RS> expectReassign(TypedSelector<RS, S> sourceSelector) {
    TypedPropertyDescriptor<RS> typedSourceProperty = getTypedPropertyFromFieldSelector(ASSIGN,
                                                                                        getMapping().getSource(),
                                                                                        sourceSelector);
    ReassignAssertBuilder<S, D, RS> reassignBuilder = new ReassignAssertBuilder<S, D, RS>(typedSourceProperty,
                                                                                          getMapping().getDestination(),
                                                                                          this);
    return reassignBuilder;
  }

  public <RD, RS> ReplaceAssertBuilder<S, D, RD, RS> expectReplace(TypedSelector<RS, S> sourceSelector,
      TypedSelector<RD, D> destinationSelector) {
    TypedPropertyDescriptor<RS> sourceProperty = getTypedPropertyFromFieldSelector(TRANSFORM, getMapping().getSource(),
                                                                                   sourceSelector);
    TypedPropertyDescriptor<RD> destProperty = getTypedPropertyFromFieldSelector(TRANSFORM,
                                                                                 getMapping().getDestination(),
                                                                                 destinationSelector);

    ReplaceAssertBuilder<S, D, RD, RS> builder = new ReplaceAssertBuilder<>(sourceProperty, destProperty, this);
    return builder;
  }

  private void _add(Transformation transformation) {
    if (assertedTransformations.contains(transformation)) {
      throw new AssertionError(TRANSFORMATION_ALREADY_ADDED);
    }
    assertedTransformations.add(transformation);
  }

  public AssertMapping<S, D> expectOmitInSource(FieldSelector<S> sourceSelector) {
    // Omit in destination
    PropertyDescriptor propertyDescriptor = getPropertyFromFieldSelector(OMIT_FIELD_SOURCE, getMapping().getSource(),
                                                                         sourceSelector);
    OmitTransformation omitSource = omitSource(getMapping(), propertyDescriptor);
    _add(omitSource);
    return this;
  }

  public AssertMapping<S, D> expectOmitInDestination(FieldSelector<D> destinationSelector) {
    PropertyDescriptor propertyDescriptor = getPropertyFromFieldSelector(OMIT_FIELD_DEST, getMapping().getDestination(),
                                                                         destinationSelector);
    OmitTransformation omitDestination = omitDestination(getMapping(), propertyDescriptor);
    _add(omitDestination);
    return this;
  }

  /**
   * Performes the specified assertions against sample objects that will be created by the assertion library.
   * 
   * @throws AssertionError
   *           Thrown if an assertion made about the {@link Mapper} object failed.
   */
  public void ensure() throws AssertionError {
    checkReplaceTransformations();
    checkTransformations();
  }

  @SuppressWarnings("rawtypes")
  private void checkReplaceTransformations() {
    Set<Transformation> mappings = mapper.getMapping()
                                         .getMappings();

    mappings.stream()
            .filter(t -> {
              return (t instanceof ReplaceTransformation);
            })
            .map(t -> {
              return (ReplaceTransformation) t;
            })
            .forEach(replace -> {

              @SuppressWarnings("rawtypes")
              Optional<ReplaceTransformation> sameTransformation = assertedTransformations.stream()
                                                                                          .filter(t -> {
                                                                                            return (t instanceof ReplaceTransformation);
                                                                                          })
                                                                                          .map(t -> {
                                                                                            return (ReplaceTransformation) t;
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
                ReplaceTransformation assertedReplaceTransformation = sameTransformation.get();
                // Check if the configured replace transformation has the same skip-null configuration than the asserted
                // one and throw if not
                if (replace.isSkipWhenNull() != assertedReplaceTransformation.isSkipWhenNull()) {
                  throw new AssertionError("The replace transformation specified by the mapper has a different null value strategy than the expected transformation:\n"
                      + replace.toString() + "\n" + assertedTransformations.toString());
                }
              }

            });

  }

  private void checkTransformations() {
    Set<Transformation> mappings = getMapping().getMappings();
    // we have to check that the mapping list contains all asserted transformations
    mappings.removeAll(assertedTransformations);
    if (!mappings.isEmpty()) {
      // if there are more elements left, the remaining transformations must be MapTransformations
      for (Transformation t : mappings) {
        if (!(t instanceof MapTransformation)) {
          throw new AssertionError("An unexpected transformation was specified on the mapping:\n" + t.toString());
        }
      }
    }
  }

  void addAssertion(Transformation transformation) {
    _add(transformation);
  }

  Mapping<S, D> getMapping() {
    return mapper.getMapping();
  }

}
