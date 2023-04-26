package com.remondis.remap;

import static org.junit.Assert.assertEquals;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.junit.Test;

import com.remondis.remap.basic.A;
import com.remondis.remap.basic.B;
import com.remondis.remap.utils.propertywalker.BiRecursivePropertyWalker;
import com.remondis.remap.utils.propertywalker.PropertyAccess;
import com.remondis.remap.utils.propertywalker.VisitorFunction;

public class BiRecursivePropertyWalkerTest {

  @Test
  public void shouldTraverseProperties() {
    Function<A, String> getter = A::getString;
    BiConsumer<A, String> setter = A::setString;

    BiRecursivePropertyWalker<A, A> walker = BiRecursivePropertyWalker.create(A.class)
        .addProperty(A::getString, A::setString, new VisitorFunction<A, String>() {

          @Override
          public void consume(PropertyAccess<A, String> access) {
            access.sourceProperty()
                .set("changed1");
            access.targetProperty()
                .set("changed2");
          }
        })
        .goInto(A::getB, A::setB, B.class)
        .addProperty(B::getString, B::setString, new VisitorFunction<B, String>() {

          @Override
          public void consume(PropertyAccess<B, String> access) {
            access.sourceProperty()
                .set("changed3");
            access.targetProperty()
                .set("changed4");
          }
        })
        .build();

    A a1 = new A("moreInA1", "stringA1", 1, 2, 1L, new B("stringB1", 1, 1));
    A a2 = new A("moreInA2", "stringA2", 1, 2, 1L, new B("stringB2", 1, 1));
    walker.execute(a1, a2);

    assertEquals("changed1", a1.getString());
    assertEquals("changed2", a2.getString());
    assertEquals("changed3", a1.getB()
        .getString());
    assertEquals("changed4", a2.getB()
        .getString());
  }
}
