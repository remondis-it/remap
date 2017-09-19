package com.remondis.remap.collections;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.remondis.remap.B;
import com.remondis.remap.BResource;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;

public class CollectionsTest {

  /**
   * There was a bug in collection mappings. It was possible to declare a
   * collection mapping while the mapper for the specified list elements was
   * missing.
   */
  @Test(expected = MappingException.class)
  public void shouldCheckForRequiredMappers() {
    Mapping.from(A.class)
      .to(AResource.class)
      .mapper();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void shouldMapNestedCollections() {

    Mapper<B, BResource> bMapper = Mapping.from(B.class)
      .to(BResource.class)
      .mapper();
    Mapper<A, AResource> aMapper = Mapping.from(A.class)
      .to(AResource.class)
      .useMapper(bMapper)
      .mapper();

    String[] stringsArr = new String[] {
        "A", "B", "C", "D"
    };

    String b1String = "b1String";
    int b1Number = 101;
    Integer b1Integer = 201;
    B b1 = new B(b1String, b1Number, b1Integer);

    String b2String = "b2String";
    int b2Number = 331;
    Integer b2Integer = 441;
    B b2 = new B(b2String, b2Number, b2Integer);

    A a = new A();
    a.addStrings(stringsArr);
    a.addBs(b1, b2);

    List<B> firstList = Arrays.asList(b1);
    List<B> secondList = Arrays.asList(b2);
    a.addNestedLists(firstList, secondList);

    AResource ar = aMapper.map(a);
    assertEquals(a.getStrings(), ar.getStrings());
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

  @SuppressWarnings("unchecked")
  @Test
  public void shouldMapCollections() {

    Mapper<B, BResource> bMapper = Mapping.from(B.class)
      .to(BResource.class)
      .mapper();
    Mapper<A, AResource> aMapper = Mapping.from(A.class)
      .to(AResource.class)
      .useMapper(bMapper)
      .omitInSource(A::getNestedLists)
      .omitInDestination(AResource::getNestedLists)
      .mapper();

    String[] stringsArr = new String[] {
        "A", "B", "C", "D"
    };

    String b1String = "b1String";
    int b1Number = 101;
    Integer b1Integer = 201;
    B b1 = new B(b1String, b1Number, b1Integer);

    String b2String = "b2String";
    int b2Number = 331;
    Integer b2Integer = 441;
    B b2 = new B(b2String, b2Number, b2Integer);

    A a = new A();
    a.addStrings(stringsArr);
    a.addBs(b1, b2);

    List<B> firstList = Arrays.asList(b1);
    List<B> secondList = Arrays.asList(b2);
    a.addNestedLists(firstList, secondList);

    AResource ar = aMapper.map(a);
    assertEquals(a.getStrings(), ar.getStrings());
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
