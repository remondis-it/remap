package com.remondis.remap.regression.capitalLetterBug;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

class MapperTest {

  private static final String STRING = "aString";

  @Test
  void shouldMapProperties() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getAString)
        .to(AResource::getAString)
        .reassign(A::getAString)
        .to(AResource::getBString)
        .mapper();

    A a = new A(STRING);
    AResource ar = mapper.map(a);

    assertEquals(STRING, ar.getAString());
    assertEquals(STRING, ar.getBString());
  }

}
