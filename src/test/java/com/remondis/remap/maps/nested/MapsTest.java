package com.remondis.remap.maps.nested;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;

public class MapsTest {

  @Test
  public void shouldDoMapperValidation() {
    Mapper<A1, A1Mapped> a1Mapper = Mapping.from(A1.class)
        .to(A1Mapped.class)
        .mapper();

    Mapper<A2, A2Mapped> a2Mapper = Mapping.from(A2.class)
        .to(A2Mapped.class)
        .mapper();

    Mapper<A3, A3Mapped> a3Mapper = Mapping.from(A3.class)
        .to(A3Mapped.class)
        .mapper();

    assertThatThrownBy(() -> Mapping.from(A.class)
        .to(AMapped.class)
        .useMapper(a2Mapper)
        .useMapper(a3Mapper)
        .mapper()).isInstanceOf(MappingException.class)
            .hasMessageContaining(
                "No mapper found for type mapping from com.remondis.remap.maps.nested.A1 to com.remondis.remap.maps.nested.A1Mapped.");

    assertThatThrownBy(() -> Mapping.from(A.class)
        .to(AMapped.class)
        .useMapper(a1Mapper)
        .useMapper(a3Mapper)
        .mapper()).isInstanceOf(MappingException.class)
            .hasMessageContaining(
                "No mapper found for type mapping from com.remondis.remap.maps.nested.A2 to com.remondis.remap.maps.nested.A2Mapped.");

    assertThatThrownBy(() -> Mapping.from(A.class)
        .to(AMapped.class)
        .useMapper(a1Mapper)
        .useMapper(a2Mapper)
        .mapper()).isInstanceOf(MappingException.class)
            .hasMessageContaining(
                "No mapper found for type mapping from com.remondis.remap.maps.nested.A3 to com.remondis.remap.maps.nested.A3Mapped.");
  }

  @Test
  public void shouldMapNestedKeyValues() {
    A1 a1 = new A1("key1");
    A2 key1 = new A2("value-key1");
    A3 value1 = new A3("value-value1");

    A1 a2 = new A1("key2");
    A2 key2 = new A2("value-key2");
    A3 value2 = new A3("value-value2");

    Mapper<A1, A1Mapped> a1Mapper = Mapping.from(A1.class)
        .to(A1Mapped.class)
        .mapper();

    Mapper<A2, A2Mapped> a2Mapper = Mapping.from(A2.class)
        .to(A2Mapped.class)
        .mapper();

    Mapper<A3, A3Mapped> a3Mapper = Mapping.from(A3.class)
        .to(A3Mapped.class)
        .mapper();

    Mapper<A, AMapped> mapper = Mapping.from(A.class)
        .to(AMapped.class)
        .useMapper(a1Mapper)
        .useMapper(a2Mapper)
        .useMapper(a3Mapper)
        .mapper();

    A a = new A();
    a.add(asList(a1), asMap(key1, value1));
    a.add(asList(a2), asMap(key2, value2));

    AMapped mapped = mapper.map(a);

    assertThat(mapped.getMap()).isInstanceOf(Map.class);
    assertThat(mapped.getMap()
        .entrySet()
        .iterator()
        .next()
        .getKey()).isInstanceOf(List.class);
    assertThat(mapped.getMap()
        .entrySet()
        .iterator()
        .next()
        .getKey()
        .iterator()
        .next()).isInstanceOf(A1Mapped.class);
    assertThat(mapped.getMap()
        .entrySet()
        .iterator()
        .next()
        .getValue()).isInstanceOf(Map.class);
    assertThat(mapped.getMap()
        .entrySet()
        .iterator()
        .next()
        .getValue()
        .entrySet()
        .iterator()
        .next()
        .getKey()).isInstanceOf(A2Mapped.class);
    assertThat(mapped.getMap()
        .entrySet()
        .iterator()
        .next()
        .getValue()
        .entrySet()
        .iterator()
        .next()
        .getValue()).isInstanceOf(A3Mapped.class);

  }

  private Map<A2, A3> asMap(A2 key, A3 value) {
    Map<A2, A3> map = new Hashtable<A2, A3>();
    map.put(key, value);
    return map;
  }

}
