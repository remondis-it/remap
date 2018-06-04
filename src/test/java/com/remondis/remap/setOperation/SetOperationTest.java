package com.remondis.remap.setOperation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.util.function.Function;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class SetOperationTest {

  private static final String STRING = "string";
  private static final String ANOTHER_STRING = "anotherString";

  @Test
  public void shouldDetectMissingSetWithSupplierAssert() {
    A a = a();
    Mapper<A, B> aToBmapper = aToBmapper();
    B b = aToBmapper.map(a);
    assertMappingResult(a, b);
    assertThatThrownBy(() -> {
      AssertMapping.of(aToBmapper)
          .expectOmitInSource(A::getAnotherString)
          .expectSet(B::getInteger)
          .withFunction(valueFunction())
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessage("The following unexpected transformation were specified on the mapping:\n"
            + "- Set Property 'integerRef' in com.remondis.remap.setOperation.B with a custom value supplier.\n");
  }

  @Test
  public void shouldDetectMissingSetWithFunctionAssert() {
    A a = a();
    Mapper<A, B> aToBmapper = aToBmapper();
    B b = aToBmapper.map(a);
    assertMappingResult(a, b);
    assertThatThrownBy(() -> {
      AssertMapping.of(aToBmapper)
          .expectOmitInSource(A::getAnotherString)
          .expectSet(B::getIntegerRef)
          .withSupplier()
          .ensure();
    }).isInstanceOf(AssertionError.class)
        .hasMessage("The following unexpected transformation were specified on the mapping:\n"
            + "- Set Property 'integer' in com.remondis.remap.setOperation.B with a custom value supplier.\n");
  }

  @Test
  public void shouldMapAndSetCorrectly() {
    A a = a();
    Mapper<A, B> aToBmapper = aToBmapper();
    B b = aToBmapper.map(a);
    assertMappingResult(a, b);
    AssertMapping.of(aToBmapper)
        .expectOmitInSource(A::getAnotherString)
        .expectSet(B::getInteger)
        .withFunction(valueFunction())
        .expectSet(B::getIntegerRef)
        .withSupplier()
        .ensure();
  }

  private void assertMappingResult(A aSrc, B bDest) {
    assertEquals(aSrc.getString(), bDest.getString());
    assertEquals(aSrc.getAnotherString()
        .length(), bDest.getInteger());
    assertEquals((Object) 100, bDest.getIntegerRef());
  }

  private Mapper<A, B> aToBmapper() {
    return Mapping.from(A.class)
        .to(B.class)
        .omitInSource(A::getAnotherString)
        .set(B::getInteger)
        .with(valueFunction())
        .set(B::getIntegerRef)
        .with(() -> 100)
        .mapper();
  }

  private A a() {
    return A.builder()
        .string(STRING)
        .anotherString(ANOTHER_STRING)
        .build();
  }

  private Function<A, Integer> valueFunction() {
    return a -> a.getAnotherString()
        .length();
  }
}
