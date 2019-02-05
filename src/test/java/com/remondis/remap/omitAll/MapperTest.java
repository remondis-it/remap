package com.remondis.remap.omitAll;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MapperTest {

  @Test
  public void shouldOmitAll() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .replace(A::getId, AResource::getId)
        .withSkipWhenNull(String::valueOf)
        .reassign(A::getDescription)
        .to(AResource::getName)
        .omitAll() // Omit all should add omits for a,b,c,d,e
        .mapper();
    System.out.println(mapper);

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
}
