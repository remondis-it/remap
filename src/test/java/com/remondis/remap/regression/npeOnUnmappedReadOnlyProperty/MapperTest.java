package com.remondis.remap.regression.npeOnUnmappedReadOnlyProperty;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MapperTest {
  @Test
  public void test() {
    Mapper<A, B> mapper = Mapping.from(A.class)
        .to(B.class)
        .mapper();

    A a = new A("string", 99);
    B bAfter = mapper.map(a);
    assertEquals(a.getString(), bAfter.getString());
    AssertMapping.of(mapper)
        .ensure();
  }
}
