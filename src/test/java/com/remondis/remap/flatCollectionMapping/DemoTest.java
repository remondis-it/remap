package com.remondis.remap.flatCollectionMapping;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

class DemoTest {

  @Test
  void replaceCollection() {
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .replaceCollection(Source::getIds, Destination::getIds)
        .with(newId())
        .mapper();

    AssertMapping.of(mapper)
        .expectReplaceCollection(Source::getIds, Destination::getIds)
        .andTest(newId())
        .ensure();
  }

  public Function<Long, Id> newId() {
    return id -> new Id(id);
  }

}
