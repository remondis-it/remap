package com.remondis.remap.nullvalues;

import java.util.concurrent.atomic.AtomicBoolean;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapperTest {

  private static final Long ZAHL_IN_A = -88L;
  private static final Integer B_INTEGER = -999;
  private static final int B_NUMBER = 222;
  private static final String B_STRING = "b string";
  private static final Integer INTEGER = 310;
  private static final int NUMBER = 210;
  private static final String STRING = "a string";

  @Test
  void shouldSkipReplaceNullValues() {
    AtomicBoolean called = new AtomicBoolean(false);
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .replace(A::getMoreInA, AResource::getMoreInAResource)
        .withSkipWhenNull(str -> {
          called.set(true);
          return str;
        })
        .reassign(A::getNamedB)
        .to(AResource::getOtherNamedB)
        .reassign(A::getZahlInA)
        .to(AResource::getZahlInAResource)
        .useMapper(Mapping.from(B.class)
            .to(BResource.class)
            .mapper())
        .mapper();

    B b = new B(B_STRING, B_NUMBER, B_INTEGER);
    A a = new A(null, STRING, NUMBER, INTEGER, ZAHL_IN_A, b);
    a.setZahlInA(ZAHL_IN_A);
    a.setNamedB(null);

    AResource ar = mapper.map(a);
    assertFalse(called.get());

    assertNull(a.getMoreInA());
    assertNull(ar.getMoreInAResource());

    assertNull(a.getNamedB());
    assertNull(ar.getOtherNamedB());

    assertEquals(STRING, a.getString());
    assertEquals(STRING, ar.getString());
    assertEquals(NUMBER, a.getNumber());
    assertEquals(NUMBER, ar.getNumber());
    assertEquals(INTEGER, a.getInteger());
    assertEquals(INTEGER, ar.getInteger());
    assertEquals(ZAHL_IN_A, a.getZahlInA());
    assertEquals(ZAHL_IN_A, ar.getZahlInAResource());

    BResource br = ar.getB();
    assertEquals(B_STRING, b.getString());
    assertEquals(B_STRING, br.getString());
    assertEquals(B_NUMBER, b.getNumber());
    assertEquals(B_NUMBER, br.getNumber());
    assertEquals(B_INTEGER, b.getInteger());
    assertEquals(B_INTEGER, br.getInteger());
  }

  @Test
  void shouldNotSkipReplaceNullValues() {
    AtomicBoolean called = new AtomicBoolean(false);
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .replace(A::getMoreInA, AResource::getMoreInAResource)
        .with(str -> {
          called.set(true);
          return str;
        })
        .reassign(A::getNamedB)
        .to(AResource::getOtherNamedB)
        .reassign(A::getZahlInA)
        .to(AResource::getZahlInAResource)
        .useMapper(Mapping.from(B.class)
            .to(BResource.class)
            .mapper())
        .mapper();

    B b = new B(B_STRING, B_NUMBER, B_INTEGER);
    A a = new A(null, STRING, NUMBER, INTEGER, ZAHL_IN_A, b);
    a.setZahlInA(ZAHL_IN_A);
    a.setNamedB(null);

    AResource ar = mapper.map(a);
    assertTrue(called.get());

    assertNull(a.getMoreInA());
    assertNull(ar.getMoreInAResource());

    assertNull(a.getNamedB());
    assertNull(ar.getOtherNamedB());

    assertEquals(STRING, a.getString());
    assertEquals(STRING, ar.getString());
    assertEquals(NUMBER, a.getNumber());
    assertEquals(NUMBER, ar.getNumber());
    assertEquals(INTEGER, a.getInteger());
    assertEquals(INTEGER, ar.getInteger());
    assertEquals(ZAHL_IN_A, a.getZahlInA());
    assertEquals(ZAHL_IN_A, ar.getZahlInAResource());

    BResource br = ar.getB();
    assertEquals(B_STRING, b.getString());
    assertEquals(B_STRING, br.getString());
    assertEquals(B_NUMBER, b.getNumber());
    assertEquals(B_NUMBER, br.getNumber());
    assertEquals(B_INTEGER, b.getInteger());
    assertEquals(B_INTEGER, br.getInteger());
  }

  @Test
  void shouldReassignNullValue() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getMoreInA)
        .to(AResource::getMoreInAResource)
        .reassign(A::getNamedB)
        .to(AResource::getOtherNamedB)
        .reassign(A::getZahlInA)
        .to(AResource::getZahlInAResource)
        .useMapper(Mapping.from(B.class)
            .to(BResource.class)
            .mapper())
        .mapper();

    B b = new B(B_STRING, B_NUMBER, B_INTEGER);
    A a = new A(null, STRING, NUMBER, INTEGER, ZAHL_IN_A, b);
    a.setZahlInA(ZAHL_IN_A);
    a.setNamedB(null);

    AResource ar = mapper.map(a);

    assertNull(a.getMoreInA());
    assertNull(ar.getMoreInAResource());

    assertNull(a.getNamedB());
    assertNull(ar.getOtherNamedB());

    assertEquals(STRING, a.getString());
    assertEquals(STRING, ar.getString());
    assertEquals(NUMBER, a.getNumber());
    assertEquals(NUMBER, ar.getNumber());
    assertEquals(INTEGER, a.getInteger());
    assertEquals(INTEGER, ar.getInteger());
    assertEquals(ZAHL_IN_A, a.getZahlInA());
    assertEquals(ZAHL_IN_A, ar.getZahlInAResource());

    BResource br = ar.getB();
    assertEquals(B_STRING, b.getString());
    assertEquals(B_STRING, br.getString());
    assertEquals(B_NUMBER, b.getNumber());
    assertEquals(B_NUMBER, br.getNumber());
    assertEquals(B_INTEGER, b.getInteger());
    assertEquals(B_INTEGER, br.getInteger());
  }

}
