package com.remondis.remap.collections;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * A collection containing a <code>null</code> element is normal, valid data (e.g. a sparse list) and must not cause
 * the mapper to throw: <code>null</code> elements are passed through as <code>null</code> in the destination
 * collection, just like a <code>null</code> value would be for a non-collection field.
 */
class NullInCollectionMappingTest {

  static class Source {
    private List<String> stringList;

    public Source(List<String> stringList) {
      this.stringList = stringList;
    }

    public List<String> getStringList() {
      return stringList;
    }
  }

  static class Destination {
    private List<String> stringList;

    public Destination() {
    }

    public void setStringList(List<String> stringList) {
      this.stringList = stringList;
    }

    public List<String> getStringList() {
      return stringList;
    }
  }

  @Test
  void testMappingWithNullInCollection() {
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .mapper();

    Source source = new Source(singletonList(null));

    Destination destination = mapper.map(source);

    assertThat(destination.getStringList()).containsExactly((String) null);
  }

  @Test
  void testMappingWithNullAmongOtherElements() {
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .mapper();

    Source source = new Source(Arrays.asList("a", null, "b"));

    Destination destination = mapper.map(source);

    assertThat(destination.getStringList()).containsExactly("a", null, "b");
  }
}