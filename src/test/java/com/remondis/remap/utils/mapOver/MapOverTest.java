package com.remondis.remap.utils.mapOver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.remondis.remap.basic.A;
import com.remondis.remap.basic.B;

public class MapOverTest {
  @Test
  public void shouldMapFromSourceToTarget() {
    A a1 = new A("moreInA1", "stringA1", 1, 2, 1L, new B("stringB1", 1, 1));
    A a2 = new A("moreInA2", "stringA2", 1, 2, 1L, new B("stringB2", 1, 1));

    assertNotEquals(a1.getString(), a2.getString());
    assertNotEquals(a1.getB()
        .getString(),
        a2.getB()
            .getString());

    MapOver<A, A> mapOver = MapOver.create(A.class)
        .mapProperty(A::getString, A::setString)
        .goInto(A::getB, A::setB, B.class)
        .mapProperty(B::getString, B::setString)
        .build();

    mapOver.mapOver(a1, a2);

    assertEquals(a1.getString(), a2.getString());
    assertEquals(a1.getB()
        .getString(),
        a2.getB()
            .getString());
  }
}
