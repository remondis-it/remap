package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;
import static java.util.Objects.nonNull;

import java.util.Optional;
import java.util.function.Consumer;

/**
 * Builder to assert a set operation on a {@link Mapper} object using {@link AssertConfiguration}.
 *
 * @param <S> The source type
 * @param <D> The destination type
 * @param <RD> The type of the destination field
 * @author schuettec
 */
public class RestructureAssertBuilder<S, D, RD> {

  private TypedPropertyDescriptor<RD> destProperty;
  private AssertConfiguration<S, D> asserts;

  RestructureAssertBuilder(TypedPropertyDescriptor<RD> destProperty, AssertConfiguration<S, D> asserts) {
    super();
    this.destProperty = destProperty;
    this.asserts = asserts;
  }

  /**
   * Expects a restructure mapping using an implicit mapping.
   *
   * @return Returns the {@link AssertConfiguration} for further configuration.
   */
  public AssertConfiguration<S, D> implicitly() {
    return _assertRestructure(null);
  }

  /**
   * Expects a restructure-mapping with the specified mapping configuration.
   *
   * @param restructureMappingAssertions The assertions made about the mapper used for restructuring.
   * @return Returns the {@link AssertConfiguration} for further configuration.
   */
  public AssertConfiguration<S, D> applying(
      Consumer<RestructuringAssertConfiguration<S, RD>> restructureMappingAssertions) {
    denyNull("restructureMappingAssertions", restructureMappingAssertions);
    return _assertRestructure(restructureMappingAssertions);
  }

  @SuppressWarnings({
      "rawtypes", "unchecked"
  })
  private AssertConfiguration<S, D> _assertRestructure(
      Consumer<RestructuringAssertConfiguration<S, RD>> restructureMappingAssertions) {
    Optional<RestructureTransformation> restructureTransformation = asserts.getMapping()
        .getMappings()
        .stream()
        .filter(t -> t instanceof RestructureTransformation)
        .map(t -> (RestructureTransformation) t)
        .filter(t -> t.getDestinationProperty()
            .equals(destProperty.property))
        // RestructureTransformation cannot appear more than once,
        // because we are streaming over a set and RestructureTransformation
        // inherits equals/hashCode from Transformation.
        .findFirst();
    if (restructureTransformation.isPresent()) {
      asserts
          .addVerification(new RestructureVerification(restructureTransformation.get(), restructureMappingAssertions));
    }
    asserts.addAssertion(new RestructureTransformation<>(asserts.getMapping(), null, destProperty.property, null,
        nonNull(restructureMappingAssertions)));
    return asserts;
  }

}
