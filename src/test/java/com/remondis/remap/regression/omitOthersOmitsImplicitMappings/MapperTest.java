package com.remondis.remap.regression.omitOthersOmitsImplicitMappings;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MapperTest {

  /**
   * The bug was, that omitOtherSourceProperties() removed all implicit mappings. In this case the Mapper complained
   * about field "string1" is not being mapped.
   */
  @Test
  public void shouldNotBreakImplicitMappings() {
    Mapper<A, B> mapper = Mapping.from(A.class)
        .to(B.class)
        .replace(A::getString2, B::getString2Length)
        .withSkipWhenNull(String::length)
        .omitOtherSourceProperties()
        .omitInDestination(B::getSomeOtherString)
        .mapper();

    AssertMapping.of(mapper)
        .expectReplace(A::getString2, B::getString2Length)
        .andSkipWhenNull()
        .expectOtherSourceFieldsToBeOmitted()
        .expectOmitInDestination(B::getSomeOtherString)
        .ensure();
  }

}
