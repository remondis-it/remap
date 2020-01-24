package com.remondis.remap;

import java.beans.PropertyDescriptor;
import java.util.Optional;
import java.util.function.Consumer;

import static com.remondis.remap.Properties.asString;
import static java.util.Objects.nonNull;

public class RestructureVerification<S, RD> implements AssertVerification {

  public static final String APPLY_SPECIFIC = "apply specific mapping configuration";
  public static final String CREATE_IMPLICIT = "create implicit mapping";
  private final RestructureTransformation restructureTransformation;
  private final Consumer<RestructuringAssertConfiguration<S, RD>> restructureMappingAssertions;
  private final boolean applyingSpecificConfiguration;

  RestructureVerification(RestructureTransformation restructureTransformation,
      Consumer<RestructuringAssertConfiguration<S, RD>> restructureMappingAssertions) {
    this.restructureTransformation = restructureTransformation;
    this.restructureMappingAssertions = restructureMappingAssertions;
    this.applyingSpecificConfiguration = nonNull(restructureMappingAssertions);
  }

  @Override
  public void verify() throws AssertionError {
    Mapper mapper = restructureTransformation.getRestructureMapper();
    boolean actual = restructureTransformation.isApplyingSpecificConfiguration();
    boolean expected = this.applyingSpecificConfiguration;
    if (expected != actual) {
      PropertyDescriptor destinationProperty = restructureTransformation.getDestinationProperty();
      throw new AssertionError(String.format(
          "Mapping from source type %s used for restructuring of field %s was configured to %s but was expected to %s.",
          mapper.getMapping()
              .getSource()
              .getName(),
          asString(destinationProperty, true), (actual ? APPLY_SPECIFIC : CREATE_IMPLICIT),
          (expected ? APPLY_SPECIFIC : CREATE_IMPLICIT)));
    }

    AssertConfiguration assertConfig = new AssertConfiguration(mapper);

    RestructuringAssertConfiguration restructuringAssertConfiguration = new RestructuringAssertConfiguration(
        assertConfig);
    restructuringAssertConfiguration.expectOtherSourceFieldsToBeOmitted();
    if (applyingSpecificConfiguration) {
      restructureMappingAssertions.accept(restructuringAssertConfiguration);
    }
    assertConfig = restructuringAssertConfiguration.getDelegate();

    try {
      assertConfig.ensure();
    } catch (AssertionError assertionError) {
      PropertyDescriptor destinationProperty = restructureTransformation.getDestinationProperty();
      throw new AssertionError(String.format(
          "Mapping from source type %s used for restructuring of field in %s did not meet assertions:\n%s\n",
          mapper.getMapping()
              .getSource()
              .getName(),
          asString(destinationProperty, true), assertionError.getMessage()));
    }
  }
}
