package com.remondis.remap.regression.typeMappingForCollectionsBug;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.TypeMapping;
import org.junit.jupiter.api.Test;

class MapperTest {

  @Test
  void shouldMap() {
    Mapper<A, B> mapper = Mapping.from(A.class)
        .to(B.class)
        .useMapper(TypeMapping.from(List.class)
            .to(Set.class)
            .applying(HashSet::new))
        .mapper();
    B b = mapper.map(new A(asList("A", "B", "A", "A", "C", "A")));
    assertNotNull(b);
    Set<String> setOfStrings = b.getStrings();
    Set<String> expected = new HashSet<>();
    expected.add("A");
    expected.add("B");
    expected.add("C");
    assertEquals(expected, setOfStrings);
  }

  @Test
  void shouldUseReplaceCollectionInsteadOfTypeMapping() {
    Mapper<A, B> mapper = Mapping.from(A.class)
        .to(B.class)
        .useMapper(TypeMapping.from(List.class)
            .to(Set.class)
            .applying(HashSet::new))
        .replaceCollection(A::getStrings, B::getStrings)
        .with(str -> "Z")
        .mapper();
    B b = mapper.map(new A(asList("A", "B", "A", "A", "C", "A")));
    assertNotNull(b);
    Set<String> setOfStrings = b.getStrings();
    Set<String> expected = new HashSet<>();
    expected.add("Z");
    assertEquals(expected, setOfStrings);
  }

  @Test
  void shouldUseReplaceInsteadOfTypeMapping() {
    final Set<String> expected = new HashSet<>();
    expected.add("Z");

    Mapper<A, B> mapper = Mapping.from(A.class)
        .to(B.class)
        .useMapper(TypeMapping.from(List.class)
            .to(Set.class)
            .applying(HashSet::new))
        .replace(A::getStrings, B::getStrings)
        .with(list -> expected)
        .mapper();
    B b = mapper.map(new A(asList("A", "B", "A", "A", "C", "A")));
    assertNotNull(b);
    Set<String> setOfStrings = b.getStrings();
    assertEquals(expected, setOfStrings);
  }

}
