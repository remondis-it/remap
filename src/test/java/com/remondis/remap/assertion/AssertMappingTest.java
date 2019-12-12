package com.remondis.remap.assertion;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class AssertMappingTest {

  @Test
  public void shouldDenyIllegalArguments() {

    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .mapper();
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getString)
        .to(AResource::getAnotherString)
        .replace(A::getInteger, AResource::getIntegerAsString)
        .with(String::valueOf)
        .omitInSource(A::getOmitted)
        .omitInDestination(AResource::getOmitted)
        .useMapper(bMapper)
        .mapper();

    assertThatThrownBy(() -> {
      AssertMapping.of(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(A::getString)
          .to(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectReplace(null, AResource::getIntegerAsString);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectReplace(A::getInteger, null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          .expectOmitInSource(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          .expectOmitInSource(A::getOmitted)
          .expectOmitInDestination(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

  }

  @Test
  public void shouldThrowExceptionOfFunctionAsCause() {
    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .mapper();
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getString)
        .to(AResource::getAnotherString)
        .replace(A::getInteger, AResource::getIntegerAsString)
        .with(b -> {
          throw new Error("Thrown for test purposes");
        })
        .omitInSource(A::getOmitted)
        .omitInDestination(AResource::getOmitted)
        .useMapper(bMapper)
        .mapper();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          .expectOmitInSource(A::getOmitted)
          .expectOmitInDestination(AResource::getOmitted)
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessageStartingWith("Function threw an unexpected exception for transformation:")
        .hasCauseExactlyInstanceOf(Error.class);
  }

  @Test
  public void regression_checkTransformationAgainstNullCausesWrongExceptionMessage() {
    // There was a bug that causes an AssertionException with a wrong operation name
    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .mapper();
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getString)
        .to(AResource::getAnotherString)
        .replace(A::getInteger, AResource::getIntegerAsString)
        // To reproduce the bug, the specified function must throw an NPE
        .with(b -> {
          throw new NullPointerException();
        })
        .omitInSource(A::getOmitted)
        .omitInDestination(AResource::getOmitted)
        .useMapper(bMapper)
        .mapper();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          .expectOmitInSource(A::getOmitted)
          .expectOmitInDestination(AResource::getOmitted)
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessageEndingWith("with Property 'integerAsString' in AResource using transformation")
        .hasCauseExactlyInstanceOf(NullPointerException.class);
  }

  @Test
  public void shouldThrowAssertionError_multipleAssertsOperationsInvolvingSameDestinationFields() {

    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .mapper();
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getString)
        .to(AResource::getAnotherString)
        .replace(A::getInteger, AResource::getIntegerAsString)
        .with(String::valueOf)
        .omitInSource(A::getOmitted)
        .omitInDestination(AResource::getOmitted)
        .useMapper(bMapper)
        .mapper();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          .expectOmitInSource(A::getOmitted)
          .expectOmitInDestination(AResource::getOmitted)
          // This asserts an operation on an already mapped destination field.
          .expectOmitInDestination(AResource::getIntegerAsString)
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessageContaining("The following expected transformation were not specified on the mapping:")
        .hasNoCause();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          // This asserts an operation on an already mapped destination field.
          .expectReplace(A::getString, AResource::getAnotherString)
          .andTest(String::valueOf)
          .expectOmitInSource(A::getOmitted)
          .expectOmitInDestination(AResource::getOmitted)
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessageContaining("The following expected transformation were not specified on the mapping:")
        .hasNoCause();
  }

  @Test
  public void shouldThrowAssertionError_multipleAssertsOfOneOperation() {

    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .mapper();
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getString)
        .to(AResource::getAnotherString)
        .replace(A::getInteger, AResource::getIntegerAsString)
        .with(String::valueOf)
        .omitInSource(A::getOmitted)
        .omitInDestination(AResource::getOmitted)
        .useMapper(bMapper)
        .mapper();

    // Assert an error when defining expecteOmitInSource multiple times.
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectOmitInSource(A::getOmitted)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          .expectOmitInDestination(AResource::getOmitted)
          .expectOmitInSource(A::getOmitted)
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessage("The specified transformation was already added as an assertion")
        .hasNoCause();

    // Assert an error when defining expecteOmitInDestination multiple times.
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectOmitInDestination(AResource::getOmitted)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          .expectOmitInDestination(AResource::getOmitted)
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessage("The specified transformation was already added as an assertion")
        .hasNoCause();

    // Assert an error when defining expectReassign multiple times.
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          .expectOmitInSource(A::getOmitted)
          .expectOmitInDestination(AResource::getOmitted)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessage("The specified transformation was already added as an assertion")
        .hasNoCause();

    // Assert an error when defining expectReplace multiple times.
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectOmitInSource(A::getOmitted)
          .expectOmitInDestination(AResource::getOmitted)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessage("The specified transformation was already added as an assertion")
        .hasNoCause();

    // Assert an error when defining expectReplace multiple times.
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andSkipWhenNull()
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectOmitInSource(A::getOmitted)
          .expectOmitInDestination(AResource::getOmitted)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andSkipWhenNull()
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessage("The specified transformation was already added as an assertion")
        .hasNoCause();

    // Assert an error when defining expectReplace multiple times but different null-skip configuration
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectOmitInSource(A::getOmitted)
          .expectOmitInDestination(AResource::getOmitted)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andSkipWhenNull()
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessage("The specified transformation was already added as an assertion")
        .hasNoCause();

    // Assert an error when defining expectReplace multiple times but different null-skip configuration
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andSkipWhenNull()
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectOmitInSource(A::getOmitted)
          .expectOmitInDestination(AResource::getOmitted)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessage("The specified transformation was already added as an assertion")
        .hasNoCause();

  }

  @Test(expected = AssertionError.class)
  public void shouldDetectExpectedNoSkipWhenNull() {

    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .mapper();
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getString)
        .to(AResource::getAnotherString)
        .replace(A::getInteger, AResource::getIntegerAsString)
        .withSkipWhenNull(String::valueOf)
        .omitInSource(A::getOmitted)
        .omitInDestination(AResource::getOmitted)
        .useMapper(bMapper)
        .mapper();

    AssertMapping<A, AResource> asserts = AssertMapping.of(mapper)
        .expectReassign(A::getString)
        .to(AResource::getAnotherString)
        .expectReplace(A::getInteger, AResource::getIntegerAsString)
        .andTest(String::valueOf)
        .expectOmitInSource(A::getOmitted)
        .expectOmitInDestination(AResource::getOmitted);
    asserts.ensure();
  }

  @Test(expected = AssertionError.class)
  public void shouldDetectExpectedSkipWhenNull() {

    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .mapper();
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getString)
        .to(AResource::getAnotherString)
        .replace(A::getInteger, AResource::getIntegerAsString)
        .with(String::valueOf)
        .omitInSource(A::getOmitted)
        .omitInDestination(AResource::getOmitted)
        .useMapper(bMapper)
        .mapper();

    AssertMapping<A, AResource> asserts = AssertMapping.of(mapper)
        .expectReassign(A::getString)
        .to(AResource::getAnotherString)
        .expectReplace(A::getInteger, AResource::getIntegerAsString)
        .andSkipWhenNull()
        .expectOmitInSource(A::getOmitted)
        .expectOmitInDestination(AResource::getOmitted);
    asserts.ensure();
  }

  @Test
  public void shouldThrowAssertionError_missingAsserts() {
    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .mapper();
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getString)
        .to(AResource::getAnotherString)
        .replace(A::getInteger, AResource::getIntegerAsString)
        .with(String::valueOf)
        .omitInSource(A::getOmitted)
        .omitInDestination(AResource::getOmitted)
        .useMapper(bMapper)
        .mapper();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessageContaining("The following unexpected transformation were specified on the mapping:")
        .hasNoCause();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessageContaining("The following unexpected transformation were specified on the mapping:")
        .hasNoCause();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessageContaining("The following unexpected transformation were specified on the mapping:")
        .hasNoCause();

    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReassign(A::getString)
          .to(AResource::getAnotherString)
          .expectReplace(A::getInteger, AResource::getIntegerAsString)
          .andTest(String::valueOf)
          .expectOmitInSource(A::getOmitted)
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessageContaining("The following unexpected transformation were specified on the mapping:")
        .hasNoCause();

  }

  @Test
  public void shouldNotThrowAssertionError() {

    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .mapper();
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getString)
        .to(AResource::getAnotherString)
        .replace(A::getInteger, AResource::getIntegerAsString)
        .with(String::valueOf)
        .omitInSource(A::getOmitted)
        .omitInDestination(AResource::getOmitted)
        .useMapper(bMapper)
        .mapper();

    AssertMapping.of(mapper)
        .expectReassign(A::getString)
        .to(AResource::getAnotherString)
        .expectReplace(A::getInteger, AResource::getIntegerAsString)
        .andTest(String::valueOf)
        .expectOmitInSource(A::getOmitted)
        .expectOmitInDestination(AResource::getOmitted)
        .ensure();
  }
}
