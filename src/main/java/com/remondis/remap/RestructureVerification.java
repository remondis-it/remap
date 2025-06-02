package com.remondis.remap;

import static com.remondis.remap.Properties.asString;
import static java.util.Objects.nonNull;

import java.beans.PropertyDescriptor;
import java.util.function.Consumer;

@SuppressWarnings("rawtypes")
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

  @SuppressWarnings("unchecked")
  @Override
  public void verify() throws AssertionError {
    Mapper mapper = restructureTransformation.getRestructureMapper();
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
          "The mapping from source type %s\nused for restructuring of field in %s\ndid not meet assertions:\n%s",
          mapper.getMapping()
              .getSource()
              .getName(),
          asString(destinationProperty, true), assertionError.getMessage()));
    }
  }
}
