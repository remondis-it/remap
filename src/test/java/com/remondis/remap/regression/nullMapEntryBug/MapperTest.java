package com.remondis.remap.regression.nullMapEntryBug;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

/**
 * Reproduces a reliability bug: mapping a {@code Map} field whose value (or key) is <code>null</code> - normal, valid
 * data for a {@link Map} - threw a raw {@link NullPointerException}. The map conversion strategy built the resulting
 * map with {@code Collectors.toMap(...)}, which uses {@link Map#merge} internally; {@code Map.merge} explicitly
 * rejects a <code>null</code> value (and a <code>null</code> key), even though {@link java.util.HashMap} itself
 * supports both perfectly well. Fixed by building the destination map imperatively and passing <code>null</code>
 * keys/values through without invoking the (possibly nested-mapper-based) key/value conversion strategy on them.
 */
class MapperTest {

  public static class Source {
    private Map<String, String> attrs;

    public Map<String, String> getAttrs() {
      return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
      this.attrs = attrs;
    }
  }

  public static class Destination {
    private Map<String, String> attrs;

    public Map<String, String> getAttrs() {
      return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
      this.attrs = attrs;
    }
  }

  @Test
  void shouldMapNullValueInMap() {
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .mapper();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put("k1", "v1");
    attrs.put("k2", null);
    Source source = new Source();
    source.setAttrs(attrs);

    Destination destination = mapper.map(source);

    assertThat(destination.getAttrs()).containsEntry("k1", "v1")
        .containsEntry("k2", null);
  }

  @Test
  void shouldMapNullKeyInMap() {
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .mapper();

    Map<String, String> attrs = new LinkedHashMap<>();
    attrs.put(null, "v1");
    Source source = new Source();
    source.setAttrs(attrs);

    Destination destination = mapper.map(source);

    assertThat(destination.getAttrs()).containsEntry(null, "v1");
  }
}
