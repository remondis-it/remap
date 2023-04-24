package com.remondis.remap;

import org.junit.Test;

import com.remondis.remap.basic.A;
import com.remondis.remap.basic.B;

public class BiRecursivePropertyWalkerTest {
  @Test
  public void shouldTraverseProperties() {
    BiRecursivePropertyWalker<A, A> walker = BiRecursivePropertyWalker.create(A.class)
        .addProperty(A::getString, (s, t) -> System.out.println("A.getString() S: " + s + " : T:" + t))
        .goInto(A::getB, B.class)
        .addProperty(B::getString, (s, t) -> System.out.println("B.getString() S: " + s + " : T:" + t))
        .build();

    A a1 = new A("moreInA1", "stringA1", 1, 2, 1L, new B("stringB1", 1, 1));
    A a2 = new A("moreInA2", "stringA2", 1, 2, 1L, new B("stringB2", 1, 1));
    walker.execute(a1, a2);
  }
}
