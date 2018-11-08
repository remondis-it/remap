package com.remondis.remap.multimapping.replace;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MapperTest {

  private static final int EXPECTED_INT = Integer.MIN_VALUE;
  private static final int EXPECTED_NUMBER = Integer.MAX_VALUE;
  private static final String EXPECTED_STRING = "string";
  private static final String EXPECTED_A_STRING = "aString";

  @Test
  public void shouldAllowMultiMappings() {

    B b = new B(EXPECTED_STRING, EXPECTED_NUMBER, EXPECTED_INT);
    A a = new A(EXPECTED_A_STRING, b);

    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .replace(A::getB, AResource::getInteger)
        .with(B::getInteger)
        .replace(A::getB, AResource::getNumber)
        .with(B::getNumber)
        .replace(A::getB, AResource::getString)
        .with(B::getString)
        .mapper();

    AResource ar = mapper.map(a);

    assertEquals(EXPECTED_A_STRING, a.getAString());
    assertEquals(EXPECTED_A_STRING, ar.getAString());

    assertEquals(EXPECTED_INT, (int) a.getB()
        .getInteger());
    assertEquals(EXPECTED_INT, (int) ar.getInteger());

    assertEquals(EXPECTED_NUMBER, a.getB()
        .getNumber());
    assertEquals(EXPECTED_NUMBER, ar.getNumber());

    assertEquals(EXPECTED_STRING, a.getB()
        .getString());
    assertEquals(EXPECTED_STRING, ar.getString());
  }

}
