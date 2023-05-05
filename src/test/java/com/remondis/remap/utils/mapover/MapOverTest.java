package com.remondis.remap.utils.mapover;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import com.remondis.remap.basic.A;
import com.remondis.remap.basic.B;
import com.remondis.remap.basic.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MapOverTest {
  private A a1, a2;
  private B b1, b2, bNew, bOrphan;
  private C c1, c2;

  @BeforeEach
  public void setUp() {
    b1 = new B("stringB1", 1, 1);
    b2 = new B("stringB2", 2, 1);
    bOrphan = new B("stringBOrphan", 3, 3);
    bNew = new B("stringBOld", 4, 4);
    a1 = new A("moreInA1", "stringA1", 1, 1, 1L, b1);
    a2 = new A("moreInA2", "stringA2", 2, 2, 2L, b2);
    Collection<B> bList1 = new ArrayList<>(asList(b1, bNew));
    Collection<B> bList2 = new ArrayList<>(asList(b2, bOrphan));
    Map<Object, B> bMap1 = bList1.stream()
        .collect(toMap(B::getInteger, Function.identity()));
    Map<Object, B> bMap2 = bList2.stream()
        .collect(toMap(B::getInteger, Function.identity()));
    c1 = new C(bList1, bMap1);
    c2 = new C(bList2, bMap2);
  }

  @Test
  void shouldMapFromSourceToTarget() {
    assertNotEquals(a1.getString(), a2.getString());
    assertNotEquals(a1.getB()
        .getString(),
        a2.getB()
            .getString());
    assertNotEquals(a1.getInteger(), a2.getInteger());

    MapOver<A, A> mapOver = MapOver.create(A.class)
        .mapProperty(A::getString, A::setString)
        .byOverwrite()
        .goInto(A::getB, A::setB, B.class)
        .mapProperty(B::getInteger, B::setInteger)
        .byOverwrite()
        .goInto(B::getInteger, B::setInteger, Integer.class)
        //TODO parent
        .mapProperty(B::getString, B::setString)
        .byOverwrite()
        .build();

    mapOver.mapOver(a1, a2);

    assertEquals(a1.getString(), a2.getString());
    assertEquals(a1.getB()
        .getString(),
        a2.getB()
            .getString());
    assertEquals(a1.getInteger(), a2.getInteger());
  }

  @Test
  void shouldMapColelctionFromSourceToTarget() {
    // given
    MapOver<C, C> mapOver = MapOver.create(C.class)
        .mapCollection(C::getCollection, B::getInteger)
        .build();

    // when
    mapOver.mapOver(c1, c2);

    // then
    assertTrue(c1.getCollection()
        .contains(b1));
    assertFalse(c2.getCollection()
        .contains(b2));
    assertTrue(c2.getCollection()
        .contains(bNew));
    assertFalse(c2.getCollection()
        .contains(bOrphan));
  }
}
