package com.remondis.remap.regression.omitCustomGet;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MapperTest {

  @Test
  public void map() {
    Mapper<Foo, FooMapped> mapper = Mapping.from(Foo.class)
        .to(FooMapped.class)
        .omitInSource(Foo::getFilteredBars)
        .mapper();

    AssertMapping.of(mapper)
        .expectOmitInSource(Foo::getFilteredBars)
        .ensure();

  }

}
