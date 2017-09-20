package com.remondis.remap.flatCollectionMapping;

import java.util.Arrays;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

/**
 * This class documents a currently impossible mapping from a build-in/primitive type to a Java Bean. It would be nice
 * to support this kinds of mappings in a future release.
 *
 * @author schuettec
 *
 */
public class FlatCollectionMapping {

  @Test
  public void shouldMapCollectionByFunction() {
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .replaceCollection(Source::getIds, Destination::getIds)
        .with(id -> Id.builder()
            .id(id)
            .build())
        .mapper();

    Source source = Source.builder()
        .ids(Arrays.asList(1L, 2L, 3L))
        .build();
    Destination map = mapper.map(source);
    System.out.println(map);

  }

}
