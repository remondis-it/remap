package com.remondis.remap.copyObjects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

@Disabled
class CopyObjectTest {

  private static final String EXPECTED_STRING = "string";

  @Test
  void shouldCopyObjectsOfSameType() {
    B b = new B(EXPECTED_STRING);
    A a = new A(b);

    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .mapper();
    AResource ar = mapper.map(a);
    assertTrue(b == a.getB());
    assertFalse(b == ar.getB());
    assertEquals(b, ar.getB());
  }

}
