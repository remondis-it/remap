package com.remondis.remap.regression.omitCustomGet;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import org.junit.jupiter.api.Test;

class MapperTest {

  @Test
  void map() {
    Mapper<Foo, FooMapped> mapper = Mapping.from(Foo.class)
        .to(FooMapped.class)
        .omitInSource(Foo::getFilteredBars)
        .mapper();

    AssertMapping.of(mapper)
        .expectOmitInSource(Foo::getFilteredBars)
        .ensure();

  }

}
