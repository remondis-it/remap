package com.remondis.remap.regression.unsupportedConcreteCollectionTypeBug;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;

/**
 * Reproduces a "must be validated at build time" reliability bug: {@code ReflectionUtil.getCollector(Class)} checked
 * whether the destination field's declared type was itself a {@link java.util.Set}/{@link List} (e.g.
 * {@code List.class.isAssignableFrom(destinationType)}), rather than whether the concrete collection it actually
 * creates ({@link java.util.HashSet}/{@link java.util.ArrayList}) can be assigned to that field. A destination field
 * declared with a concrete collection subtype (e.g. {@link LinkedList}) satisfied the former but not the latter, so
 * {@code mapper()} build succeeded, and only the first mapped value failed - with a raw, confusing
 * {@link IllegalArgumentException} from the reflective setter call, not even a clear {@link MappingException}. Fixed
 * by checking assignability the other way round: {@code destinationType.isAssignableFrom(ArrayList/HashSet.class)}.
 */
class MapperTest {

  public static class Source {
    private List<String> tags;

    public List<String> getTags() {
      return tags;
    }

    public void setTags(List<String> tags) {
      this.tags = tags;
    }
  }

  public static class UnsupportedDestination {
    private LinkedList<String> tags;

    public LinkedList<String> getTags() {
      return tags;
    }

    public void setTags(LinkedList<String> tags) {
      this.tags = tags;
    }
  }

  public static class SupportedDestination {
    private List<String> tags;

    public List<String> getTags() {
      return tags;
    }

    public void setTags(List<String> tags) {
      this.tags = tags;
    }
  }

  @Test
  void shouldFailAtBuildTimeForConcreteUnsupportedCollectionSubtype() {
    assertThatThrownBy(() -> Mapping.from(Source.class)
        .to(UnsupportedDestination.class)
        .mapper()).isInstanceOf(MappingException.class);
  }

  @Test
  void shouldStillMapInterfaceTypedCollectionDestination() {
    Mapper<Source, SupportedDestination> mapper = Mapping.from(Source.class)
        .to(SupportedDestination.class)
        .mapper();

    Source source = new Source();
    source.setTags(Arrays.asList("a", "b"));

    SupportedDestination destination = mapper.map(source);

    assertThat(destination.getTags()).containsExactly("a", "b");
  }
}
