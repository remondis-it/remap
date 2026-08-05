package com.remondis.remap.regression.unsupportedCollectionRuntimeTypeBug;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayDeque;
import java.util.Collection;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

/**
 * Reproduces a reliability bug: {@link Mapper#map(Collection)} accepts any {@link Collection}, but internally threw a
 * {@link com.remondis.remap.MappingException} whenever the runtime type of the argument was neither a {@link
 * java.util.Set} nor a {@link java.util.List} (e.g. an {@link ArrayDeque}). Since this depends entirely on the
 * concrete collection type a caller happens to pass at a given call, it can never be validated ahead of time at
 * {@code mapper()} build time - so any such collection is now simply treated like a {@link java.util.List} instead of
 * being rejected.
 */
class MapperTest {

  public static class Source {
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  public static class Destination {
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  @Test
  void shouldMapArbitraryRuntimeCollectionTypes() {
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .mapper();

    Source source = new Source();
    source.setValue("x");
    ArrayDeque<Source> deque = new ArrayDeque<>();
    deque.add(source);

    Collection<Destination> result = mapper.map(deque);

    assertThat(result).hasSize(1);
    assertThat(result.iterator()
        .next()
        .getValue()).isEqualTo("x");
  }
}
