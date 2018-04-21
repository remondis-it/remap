package com.remondis.remap.mapInto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MapperTest {

  @Test
  public void shouldMap() {
    Mapper<A, B> mapper = Mapping.from(A.class)
        .to(B.class)
        .omitInDestination(B::getSize)
        .mapper();

    A a = new A("chris", 29);
    B b = new B("CHRIS", 28, 189);

    B afterMap = mapper.map(a, b);

    assertSame(b, afterMap);
    assertEquals(a.getName(), b.getName());
    assertEquals(a.getAge(), b.getAge());
    assertEquals(189, (int) b.getSize());

  }
}
