package com.remondis.remap.regression.lazyCollectionTypeValidationBug;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.Queue;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;

/**
 * Reproduces a "must be validated at build time" reliability bug: whether a destination collection field's type is
 * supported (only {@link java.util.Set} and {@link List} are) is static configuration knowledge - the field's type
 * never changes between mapping calls - yet {@code replaceCollection(...)} and property-path collection mappings only
 * discovered an unsupported destination type (e.g. a {@link Queue}) the first time a value was actually mapped,
 * instead of when the mapper was built. Fixed by resolving (and validating) the collector once in
 * {@code validateTransformation()}.
 */
class MapperTest {

  public static class Source {
    private List<Integer> numbers;

    public List<Integer> getNumbers() {
      return numbers;
    }

    public void setNumbers(List<Integer> numbers) {
      this.numbers = numbers;
    }
  }

  public static class Destination {
    private Queue<String> numbers;

    public Queue<String> getNumbers() {
      return numbers;
    }

    public void setNumbers(Queue<String> numbers) {
      this.numbers = numbers;
    }
  }

  @Test
  void shouldFailAtBuildTimeForUnsupportedReplaceCollectionDestinationType() {
    assertThatThrownBy(() -> Mapping.from(Source.class)
        .to(Destination.class)
        .replaceCollection(Source::getNumbers, Destination::getNumbers)
        .with(String::valueOf)
        .mapper()).isInstanceOf(MappingException.class);
  }
}
