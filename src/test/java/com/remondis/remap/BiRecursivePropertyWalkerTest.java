package com.remondis.remap;

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
            System.out.println("Source a1.getString()" + access.sourceProperty()
                .get() + " Target: "
                + access.targetProperty()
                    .get());
          }
        })
        .goInto(A::getB, A::setB, B.class)
        .addProperty(B::getString, B::setString, new VisitorFunction<B, String>() {

          @Override
          public void consume(PropertyAccess<B, String> access) {
            System.out.println("Source a1.getString()" + access.sourceProperty()
                .get() + " Target: "
                + access.targetProperty()
                    .get());
          }
        })
        .build();

    A a1 = new A("moreInA1", "stringA1", 1, 2, 1L, new B("stringB1", 1, 1));
    A a2 = new A("moreInA2", "stringA2", 1, 2, 1L, new B("stringB2", 1, 1));
    walker.execute(a1, a2);
  }
}
