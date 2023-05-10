package com.remondis.remap.collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;
import com.remondis.remap.basic.B;
import com.remondis.remap.basic.BResource;

class CollectionsTest {

  /**
   * There was a bug in collection mappings. It was possible to declare a
   * collection mapping while the mapper for the specified list elements was
   * missing.
   */
  @Test
  void shouldCheckForRequiredMappers() {
    assertThrows(MappingException.class, () -> Mapping.from(A.class)
        .to(AResource.class)
        .mapper());
  }

  @Test
  void shouldMapNestedCollections() {

    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .mapper();
    Mapper<A, AResource> aMapper = Mapping.from(A.class)
        .to(AResource.class)
        .useMapper(bMapper)
        .mapper();

    String b1String = "b1String";
    int b1Number = 101;
    Integer b1Integer = 201;
    B b1 = new B(b1String, b1Number, b1Integer);

    String b2String = "b2String";
    int b2Number = 331;
    Integer b2Integer = 441;
    B b2 = new B(b2String, b2Number, b2Integer);

    A a = new A();
    a.addBs(b1, b2);

    AResource ar = aMapper.map(a);
    List<B> bs = a.getBs();
    List<BResource> brs = ar.getBs();
    assertEquals(bs.size(), brs.size());

    BResource br1Actual = brs.get(0);
    BResource br2Actual = brs.get(1);

    assertEquals(b1String, b1.getString());
    assertEquals(b1String, br1Actual.getString());
    assertEquals(b1Number, b1.getNumber());
    assertEquals(b1Number, br1Actual.getNumber());
    assertEquals(b1Integer, b1.getInteger());
    assertEquals(b1Integer, br1Actual.getInteger());

    assertEquals(b2String, b2.getString());
    assertEquals(b2String, br2Actual.getString());
    assertEquals(b2Number, b2.getNumber());
    assertEquals(b2Number, br2Actual.getNumber());
    assertEquals(b2Integer, b2.getInteger());
    assertEquals(b2Integer, br2Actual.getInteger());

  }

  @Test
  void shouldMapCollections() {

    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .mapper();
    Mapper<A, AResource> aMapper = Mapping.from(A.class)
        .to(AResource.class)
        .useMapper(bMapper)
        .mapper();

    String b1String = "b1String";
    int b1Number = 101;
    Integer b1Integer = 201;
    B b1 = new B(b1String, b1Number, b1Integer);

    String b2String = "b2String";
    int b2Number = 331;
    Integer b2Integer = 441;
    B b2 = new B(b2String, b2Number, b2Integer);

    A a = new A();
    a.addBs(b1, b2);

    AResource ar = aMapper.map(a);
    List<B> bs = a.getBs();
    List<BResource> brs = ar.getBs();
    assertEquals(bs.size(), brs.size());

    BResource br1Actual = brs.get(0);
    BResource br2Actual = brs.get(1);

    assertEquals(b1String, b1.getString());
    assertEquals(b1String, br1Actual.getString());
    assertEquals(b1Number, b1.getNumber());
    assertEquals(b1Number, br1Actual.getNumber());
    assertEquals(b1Integer, b1.getInteger());
    assertEquals(b1Integer, br1Actual.getInteger());

    assertEquals(b2String, b2.getString());
    assertEquals(b2String, br2Actual.getString());
    assertEquals(b2Number, b2.getNumber());
    assertEquals(b2Number, br2Actual.getNumber());
    assertEquals(b2Integer, b2.getInteger());
    assertEquals(b2Integer, br2Actual.getInteger());

  }

}
