package com.remondis.remap.setOperation;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class SetOperationTest {

  private static final String STRING = "string";
  private static final String ANOTHER_STRING = "anotherString";

  @Test
  public void shouldMapAndSetCorrectly() {
    A aSrc = A.builder()
        .string(STRING)
        .anotherString(ANOTHER_STRING)
        .build();

    Mapper<A, B> mapper = Mapping.from(A.class)
        .to(B.class)
        .omitInSource(A::getAnotherString)
        .set(B::getInteger)
        .with(a -> a.getAnotherString()
            .length())
        .set(B::getIntegerRef)
        .with(() -> 100)
        .mapper();

    B bDest = mapper.map(aSrc);
    assertEquals(aSrc.getString(), bDest.getString());
    assertEquals(aSrc.getAnotherString()
        .length(), bDest.getInteger());
    assertEquals((Object) 100, bDest.getIntegerRef());

    AssertMapping.of(mapper)
        .expectOmitInSource(A::getAnotherString)
        .expectSet(B::getInteger)
        .expectSet(B::getIntegerRef)
        .ensure();
  }
}
