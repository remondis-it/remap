package com.remondis.remap.flatCollectionMapping;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.Transform;

public class DemoTest {

  @Test
  public void replaceCollection() {
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

  public Transform<Long, Id> newId() {
    return id -> Id.builder()
        .id(id)
        .build();
  }

}
