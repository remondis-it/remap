package com.remondis.remap.flatCollectionMapping;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

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
  public void shouldDetectIllegalArguments() {
    assertThatThrownBy(() -> {
      Mapping.from(Source.class)
          .to(Destination.class)
          .replaceCollection(null, Destination::getIds);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      Mapping.from(Source.class)
          .to(Destination.class)
          .replaceCollection(Source::getIds, null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();

    assertThatThrownBy(() -> {
      Mapping.from(Source.class)
          .to(Destination.class)
          .replaceCollection(Source::getIds, Destination::getIds)
          .with(null);
    }).isInstanceOf(IllegalArgumentException.class)
        .hasNoCause();
  }

  @Test
  public void shouldMapCollectionByFunction() {
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .replaceCollection(Source::getIds, Destination::getIds)
        .with(id -> Id.builder()
            .id(id)
            .build())
        .mapper();

    List<Long> expected = Arrays.asList(1L, 2L, 3L);
    Source source = Source.builder()
        .ids(expected)
        .build();
    Destination map = mapper.map(source);
    List<Id> actual = map.getIds();
    assertEquals(expected, actual);
  }

}
