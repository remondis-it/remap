package com.remondis.remap.omitOthers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import org.junit.jupiter.api.Test;

class MapperTest {

  @Test
  void shouldOmitOtherSourceProperties() {
    assertThatThrownBy(() -> Mapping.from(A.class)
        .to(AResource.class)
        .replace(A::getId, AResource::getId)
        .withSkipWhenNull(String::valueOf)
        .reassign(A::getDescription)
        .to(AResource::getName)
        .omitOtherSourceProperties()
        .mapper()).hasMessageContaining("- Property 'e' in AResource")
        .hasMessageContaining("- Property 'c' in AResource")
        .hasMessageContaining("- Property 'd' in AResource");
  }

  @Test
  void shouldOmitOtherDestinationProperties() {
    assertThatThrownBy(() -> Mapping.from(A.class)
        .to(AResource.class)
        .replace(A::getId, AResource::getId)
        .withSkipWhenNull(String::valueOf)
        .reassign(A::getDescription)
        .to(AResource::getName)
        .omitOtherDestinationProperties()
        .mapper()).hasMessageContaining("- Property 'b' in A")
        .hasMessageContaining("- Property 'a' in A");
  }

  @Test
  void shouldOmitOtherSourceAndDestFields_withExpectOtherSourceAndDestFieldsToBeOmitted() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .replace(A::getId, AResource::getId)
        .withSkipWhenNull(String::valueOf)
        .reassign(A::getDescription)
        .to(AResource::getName)
        .omitOtherSourceProperties()
        .omitOtherDestinationProperties()
        .mapper();

    AssertMapping.of(mapper)
        .expectReplace(A::getId, AResource::getId)
        .andSkipWhenNull()
        .expectReassign(A::getDescription)
        .to(AResource::getName)
        .expectOtherSourceFieldsToBeOmitted()
        .expectOtherDestinationFieldsToBeOmitted()
        .ensure();
  }

  @Test
  void shouldOmitOtherSourceAndDestFields_withExpectOthersToBeOmitted() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .replace(A::getId, AResource::getId)
        .withSkipWhenNull(String::valueOf)
        .reassign(A::getDescription)
        .to(AResource::getName)
        .omitOtherSourceProperties()
        .omitOtherDestinationProperties()
        .mapper();

    AssertMapping.of(mapper)
        .expectReplace(A::getId, AResource::getId)
        .andSkipWhenNull()
        .expectReassign(A::getDescription)
        .to(AResource::getName)
        .expectOthersToBeOmitted()
        .ensure();
  }

  @Test
  void shouldOmitOthers_withExpectOthersToBeOmitted() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .replace(A::getId, AResource::getId)
        .withSkipWhenNull(String::valueOf)
        .reassign(A::getDescription)
        .to(AResource::getName)
        .omitOthers() // Should add omits for a,b,c,d,e
        .mapper();

    AssertMapping.of(mapper)
        .expectReplace(A::getId, AResource::getId)
        .andSkipWhenNull()
        .expectReassign(A::getDescription)
        .to(AResource::getName)
        .expectOthersToBeOmitted()
        .ensure();
  }

  @Test
  void shouldOmitOthers() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .replace(A::getId, AResource::getId)
        .withSkipWhenNull(String::valueOf)
        .reassign(A::getDescription)
        .to(AResource::getName)
        .omitOthers() // Should add omits for a,b,c,d,e
        .mapper();

    AssertMapping.of(mapper)
        .expectReplace(A::getId, AResource::getId)
        .andSkipWhenNull()
        .expectReassign(A::getDescription)
        .to(AResource::getName)
        .expectOmitInSource(A::getA)
        .expectOmitInSource(A::getB)
        .expectOmitInDestination(AResource::getC)
        .expectOmitInDestination(AResource::getD)
        .expectOmitInDestination(AResource::getE)
        .ensure();
  }

  @Test
  void shouldDenyOmitOthersIfNotExpected() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .replace(A::getId, AResource::getId)
        .withSkipWhenNull(String::valueOf)
        .reassign(A::getDescription)
        .to(AResource::getName)
        .omitOthers() // should add omits for a,b,c,d,e
        .mapper();

    assertThatThrownBy(() -> AssertMapping.of(mapper)
        .expectReplace(A::getId, AResource::getId)
        .andSkipWhenNull()
        .expectReassign(A::getDescription)
        .to(AResource::getName)
        .ensure()).isInstanceOf(AssertionError.class)
        .hasMessageContaining("The following unexpected transformation were specified on the mapping:")
        .hasMessageContaining("- Omitting Property 'a' in A")
        .hasMessageContaining("- Omitting Property 'd' in AResource")
        .hasMessageContaining("- Omitting Property 'c' in AResource")
        .hasMessageContaining("- Omitting Property 'e' in AResource")
        .hasMessageContaining("- Omitting Property 'b' in A");

  }
}
