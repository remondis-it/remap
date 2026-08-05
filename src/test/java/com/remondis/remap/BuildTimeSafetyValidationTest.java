package com.remondis.remap;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Locks in a few properties that are central to the mapper's "safe to use" contract: a configuration problem that is
 * fully determined by the involved types (not by mapped data) must be rejected as early as possible, rather than only
 * surfacing once a mapping is actually performed.
 */
class BuildTimeSafetyValidationTest {

  static class Source {
    private String value;

    public String getValue() {
      return value;
    }

    public void setValue(String value) {
      this.value = value;
    }
  }

  static class NoDefaultConstructorDestination {
    private final String value;

    NoDefaultConstructorDestination(String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  /**
   * A destination type without a public no-args constructor is rejected immediately by {@link Types#to(Class)} -
   * before any further mapping configuration - rather than only surfacing once a mapping is actually performed. This
   * matters because the nested-mapper conversion strategy always creates a fresh destination instance
   * ({@code mapper.map(sourceValue, null)}), so every destination type used anywhere in a mapper graph needs a
   * working default constructor regardless of how it is used at the top level.
   */
  @Test
  void shouldRejectDestinationWithoutDefaultConstructorAtConfigurationTime() {
    assertThatThrownBy(() -> Mapping.from(Source.class)
        .to(NoDefaultConstructorDestination.class)).isInstanceOf(MappingException.class)
        .hasMessageContaining(NoDefaultConstructorDestination.class.getName());
  }

  static class MapSource {
    private Map<String, String> attrs;

    public Map<String, String> getAttrs() {
      return attrs;
    }

    public void setAttrs(Map<String, String> attrs) {
      this.attrs = attrs;
    }
  }

  static class ConcreteMapDestination {
    // The map conversion strategy always produces a java.util.LinkedHashMap; a field declared as a concrete Map
    // subtype other than one LinkedHashMap satisfies cannot actually be populated.
    private HashMap<String, String> attrs;

    public HashMap<String, String> getAttrs() {
      return attrs;
    }

    public void setAttrs(HashMap<String, String> attrs) {
      this.attrs = attrs;
    }
  }

  /**
   * A destination field declared as a concrete {@link Map} subtype other than exactly {@link Map} itself is rejected
   * at {@code mapper()} build time rather than failing with a reflection error once a value is actually mapped.
   */
  @Test
  void shouldRejectConcreteMapDestinationTypeAtBuildTime() {
    assertThatThrownBy(() -> Mapping.from(MapSource.class)
        .to(ConcreteMapDestination.class)
        .mapper()).isInstanceOf(MappingException.class);
  }
}
