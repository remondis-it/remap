package com.remondis.remap.setOperation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Function;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import org.junit.jupiter.api.Test;

class SetOperationTest {

  private static final String STATIC_STRING_VALUE = "static-string-value";
  private static final RuntimeException RUNTIME_EXCEPTION = new RuntimeException("Thrown for test purposes.");
  private static final String STRING = "string";
  private static final String ANOTHER_STRING = "anotherString";

  @Test
  void shouldHandleFunctionExceptionInAsserts() {
    A a = a();
    Mapper<A, B> aToBmapper = Mapping.from(A.class)
        .to(B.class)
        .omitInSource(A::getAnotherString)
        .set(B::getInteger)
        .with(throwingFunction())
        .set(B::getIntegerRef)
        .with(() -> 100)
        .set(B::getValueSet)
        .with(STATIC_STRING_VALUE)
        .mapper();
    AssertMapping.of(aToBmapper)
        .expectOmitInSource(A::getAnotherString)
        .expectSet(B::getInteger)
        .withFunction()
        .expectSet(B::getIntegerRef)
        .withSupplier()
        .expectSet(B::getValueSet)
        .withValue()
        .ensure();
    assertThatThrownBy(() -> {
      aToBmapper.map(a);
    }).isSameAs(RUNTIME_EXCEPTION);
  }

  @Test
  void shouldDetectMissingSetValueAssert() {
    A a = a();
    Mapper<A, B> aToBmapper = aToBmapper();
    B b = aToBmapper.map(a);
    assertMappingResult(a, b);
    assertThatThrownBy(() -> AssertMapping.of(aToBmapper)
        .expectOmitInSource(A::getAnotherString)
        .expectSet(B::getInteger)
        .withFunction()
        .expectSet(B::getIntegerRef)
        .withSupplier()
        .ensure()).isInstanceOf(AssertionError.class)
        .hasMessage("The following unexpected transformation were specified on the mapping:\n"
            + "- Set Property 'valueSet' in B with a custom value supplier.\n");
  }

  @Test
  void shouldDetectMissingSetWithSupplierAssert() {
    A a = a();
    Mapper<A, B> aToBmapper = aToBmapper();
    B b = aToBmapper.map(a);
    assertMappingResult(a, b);
    assertThatThrownBy(() -> AssertMapping.of(aToBmapper)
        .expectOmitInSource(A::getAnotherString)
        .expectSet(B::getInteger)
        .withFunction()
        .expectSet(B::getValueSet)
        .withValue()
        .ensure()).isInstanceOf(AssertionError.class)
        .hasMessage("The following unexpected transformation were specified on the mapping:\n"
            + "- Set Property 'integerRef' in B with a custom value supplier.\n");
  }

  @Test
  void shouldDetectMissingSetWithFunctionAssert() {
    A a = a();
    Mapper<A, B> aToBmapper = aToBmapper();
    B b = aToBmapper.map(a);
    assertMappingResult(a, b);
    assertThatThrownBy(() -> AssertMapping.of(aToBmapper)
        .expectOmitInSource(A::getAnotherString)
        .expectSet(B::getIntegerRef)
        .withSupplier()
        .expectSet(B::getValueSet)
        .withValue()
        .ensure()).isInstanceOf(AssertionError.class)
        .hasMessage("The following unexpected transformation were specified on the mapping:\n"
            + "- Set Property 'integer' in B with a custom value supplier.\n");
  }

  @Test
  void shouldMapAndSetCorrectly() {
    A a = a();
    Mapper<A, B> aToBmapper = aToBmapper();
    B b = aToBmapper.map(a);
    assertMappingResult(a, b);
    AssertMapping.of(aToBmapper)
        .expectOmitInSource(A::getAnotherString)
        .expectSet(B::getInteger)
        .withFunction()
        .expectSet(B::getIntegerRef)
        .withSupplier()
        .expectSet(B::getValueSet)
        .withValue()
        .ensure();
  }

  private void assertMappingResult(A aSrc, B bDest) {
    assertEquals(aSrc.getString(), bDest.getString());
    assertEquals(aSrc.getAnotherString()
        .length(), bDest.getInteger());
    assertEquals((Object) 100, bDest.getIntegerRef());
    assertEquals(STATIC_STRING_VALUE, bDest.getValueSet());
  }

  private Mapper<A, B> aToBmapper() {
    return Mapping.from(A.class)
        .to(B.class)
        .omitInSource(A::getAnotherString)
        .set(B::getInteger)
        .with(valueFunction())
        .set(B::getIntegerRef)
        .with(() -> 100)
        .set(B::getValueSet)
        .with(STATIC_STRING_VALUE)
        .mapper();
  }

  private A a() {
    return new A(STRING, ANOTHER_STRING);
  }

  private Function<A, Integer> valueFunction() {
    return a -> a.getAnotherString()
        .length();
  }

  private Function<A, Integer> throwingFunction() {
    return a -> {
      throw RUNTIME_EXCEPTION;
    };
  }
}
