package com.remondis.remap.omitOthers;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MapperTest {

  @Test
  public void shouldOmitOthers() {
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
  public void shouldDenyOmitOthersIfNotExpected() {
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
            .hasMessageContaining("- Omitting Property 'a' in com.remondis.remap.omitOthers.A")
            .hasMessageContaining("- Omitting Property 'd' in com.remondis.remap.omitOthers.AResource")
            .hasMessageContaining("- Omitting Property 'c' in com.remondis.remap.omitOthers.AResource")
            .hasMessageContaining("- Omitting Property 'e' in com.remondis.remap.omitOthers.AResource")
            .hasMessageContaining("- Omitting Property 'b' in com.remondis.remap.omitOthers.A");

  }
}
