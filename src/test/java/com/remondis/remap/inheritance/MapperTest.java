package com.remondis.remap.inheritance;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.remondis.remap.B;
import com.remondis.remap.BResource;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MapperTest {

  public static final String MORE_IN_A = "moreInA";
  public static final Long ZAHL_IN_A = -88L;
  public static final Integer B_INTEGER = -999;
  public static final int B_NUMBER = 222;
  public static final String B_STRING = "b string";
  public static final Integer INTEGER = 310;
  public static final int NUMBER = 210;
  public static final String STRING = "a string";

  /**
   * Ensures that the mapper maps inherited field correctly.
   */
  @Test
  public void shouldMapInheritedFields() {
    Mapper<Child, ChildResource> map = Mapping.from(Child.class)
        .to(ChildResource.class)
        .omitInSource(Child::getMoreInParent)
        .omitInDestination(ChildResource::getMoreInParentResource)
        .useMapper(Mapping.from(B.class)
            .to(BResource.class)
            .mapper())
        .mapper();

    B b = new B(B_STRING, B_NUMBER, B_INTEGER);
    Object shouldNotMap = new Object();
    Object object = new Object();
    int zahl = 11;
    Child child = new Child(shouldNotMap, STRING, b, object, zahl);
    ChildResource cr = map.map(child);

    assertNull(cr.getMoreInParentResource());
    assertEquals(STRING, child.getString());
    assertEquals(STRING, cr.getString());
    assertEquals(object, child.getObject());
    // We cannot assertEquals here, because it's object they will not be equal.
    assertNotNull(cr.getObject());
    assertEquals(zahl, child.getZahl());
    assertEquals(zahl, cr.getZahl());

    BResource br = cr.getB();
    assertEquals(B_STRING, b.getString());
    assertEquals(B_STRING, br.getString());
    assertEquals(B_NUMBER, b.getNumber());
    assertEquals(B_NUMBER, br.getNumber());
    assertEquals(B_INTEGER, b.getInteger());
    assertEquals(B_INTEGER, br.getInteger());

  }

  @Test
  public void shouldReuseParentMapperConfig() {
    Mapping<? extends Parent, ? extends ParentResource> parentMapping = Mapping.from(Parent.class)
        .to(ParentResource.class);
    parentMappingConfig(parentMapping);
    Mapper<? extends Parent, ? extends ParentResource> parentMapper = parentMapping.mapper();

    Mapping<Child, ChildResource> childMapping = Mapping.from(Child.class)
        .to(ChildResource.class);
    parentMappingConfig(childMapping);
    Mapper<Child, ChildResource> childMapper = childMapping.mapper();

    B b = new B(B_STRING, B_NUMBER, B_INTEGER);
    Object shouldNotMap = new Object();
    Object object = new Object();
    int zahl = 11;
    Child child = new Child(shouldNotMap, STRING, b, object, zahl);
    ChildResource cr = childMapper.map(child);

    assertEquals(child.getMoreInParent(), cr.getMoreInParentResource());
    assertNull(cr.getShouldNotMap());
    assertEquals(child.getString(), cr.getString());
    assertEquals(child.getB()
        .getInteger(),
        cr.getB()
            .getInteger());
    assertEquals(child.getB()
        .getNumber(),
        cr.getB()
            .getNumber());
    assertEquals(child.getB()
        .getString(),
        cr.getB()
            .getString());

    // We cannot assertEquals here, because it's object they will not be equal.
    assertEquals(child.getObject(), cr.getObject());
    assertEquals(child.getZahl(), cr.getZahl());

  }

  private Mapping<? extends Parent, ? extends ParentResource> parentMappingConfig(
      Mapping<? extends Parent, ? extends ParentResource> mapping) {
    return mapping.reassign(Parent::getMoreInParent)
        .to(ParentResource::getMoreInParentResource)
        .omitInSource(Parent::getShouldNotMap)
        .omitInDestination(ParentResource::getShouldNotMap)
        .useMapper(Mapping.from(B.class)
            .to(BResource.class)
            .mapper());
  }
}
