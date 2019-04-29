package com.remondis.remap.basic;

import static com.remondis.remap.basic.MapperTest.B_INTEGER;
import static com.remondis.remap.basic.MapperTest.B_NUMBER;
import static com.remondis.remap.basic.MapperTest.B_STRING;
import static com.remondis.remap.basic.MapperTest.INTEGER;
import static com.remondis.remap.basic.MapperTest.MORE_IN_A;
import static com.remondis.remap.basic.MapperTest.NUMBER;
import static com.remondis.remap.basic.MapperTest.STRING;
import static com.remondis.remap.basic.MapperTest.ZAHL_IN_A;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MapIterableTest {

  @SuppressWarnings("rawtypes")
  @Test
  public void mapFromIterable() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .omitInSource(A::getMoreInA)
        .omitInDestination(AResource::getMoreInAResource)
        .reassign(A::getZahlInA)
        .to(AResource::getZahlInAResource)
        .useMapper(Mapping.from(B.class)
            .to(BResource.class)
            .mapper())
        .mapper();

    B b = new B(B_STRING, B_NUMBER, B_INTEGER);
    A a = new A(MORE_IN_A, STRING, NUMBER, INTEGER, ZAHL_IN_A, b);
    a.setZahlInA(ZAHL_IN_A);

    A[] aarr = new A[] {
        a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a, a
    };

    List<A> aList = Arrays.asList(aarr);
    List<AResource> arCollection = mapper.map((Iterable<A>) aList);

    // Make sure this is a new collection
    assertFalse((List) aList == (List) arCollection);

    assertEquals(aarr.length, aList.size());
    assertEquals(aarr.length, arCollection.size());

    for (AResource ar : arCollection) {
      assertNull(ar.getMoreInAResource());
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

}
