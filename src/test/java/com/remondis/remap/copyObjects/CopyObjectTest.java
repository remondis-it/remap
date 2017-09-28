package com.remondis.remap.copyObjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

@Ignore
public class CopyObjectTest {

  private static final String EXPECTED_STRING = "string";

  @Test
  public void shouldCopyObjectsOfSameType() {
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
