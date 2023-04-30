package com.remondis.remap.noImplicitMappings;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class NoImplicitMappingsTest {

  private Mapper<B, B> bMapper;

  @BeforeEach
  public void setup() {
    this.bMapper = Mapping.from(B.class)
        .to(B.class)
        .mapper();

  }

  private Mapper<A, A> getAMapper() {
    return Mapping.from(A.class)
        .to(A.class)
        .useMapper(bMapper)
        .noImplicitMappings()
        .reassign(A::getBs)
        .to(A::getBs)
        .reassign(A::getInteger)
        .to(A::getInteger)
        .replace(A::getMap, A::getMap)
        .withSkipWhenNull(Function.identity())
        .reassign(A::getString)
        .to(A::getString)
        .mapper();
  }

  @Test
  void shouldMapRight() {
    int expectedBInt = 42;
    String expectedBString = "string";
    B b = new B(expectedBString, expectedBInt);
    String aString = "aString";
    int expectedAInt = 43;
    Map<String, String> expectedMap = new HashMap<>();
    String expectedMapKey = "string1";
    String expectedMapValue = "value1";
    expectedMap.put(expectedMapKey, expectedMapValue);

    A a = new A(asList(b), aString, expectedAInt, expectedMap);
    A mapped = getAMapper().map(a);
    assertEquals(a, mapped);
  }

  @Test
  void shouldAssertHappyPath() {
    AssertMapping.of(getAMapper())
        .expectNoImplicitMappings()
        .expectReassign(A::getBs)
        .to(A::getBs)
        .expectReassign(A::getInteger)
        .to(A::getInteger)
        .expectReplace(A::getMap, A::getMap)
        .andSkipWhenNull()
        .expectReassign(A::getString)
        .to(A::getString)
        .ensure();
  }

  @Test
  void shouldComplainAboutDifferentImplicitMappingStrategy_expectImplicit() {
    assertThatThrownBy(() -> AssertMapping.of(getAMapper())
        .expectReassign(A::getBs)
        .to(A::getBs)
        .expectReassign(A::getInteger)
        .to(A::getInteger)
        .expectReplace(A::getMap, A::getMap)
        .andSkipWhenNull()
        .expectReassign(A::getString)
        .to(A::getString)
        .ensure()).isInstanceOf(AssertionError.class)
        .hasMessage("The mapper was expected to create implicit mappings but the actual mapper does not.");
  }

  @Test
  void shouldComplainAboutDifferentImplicitMappingStrategy_expectNoImplicit() {
    Mapper<A, A> implicitMapper = Mapping.from(A.class)
        .to(A.class)
        .useMapper(bMapper)
        .replace(A::getMap, A::getMap)
        .withSkipWhenNull(Function.identity())
        .mapper();

    assertThatThrownBy(() -> AssertMapping.of(implicitMapper)
        .expectNoImplicitMappings()
        .expectReplace(A::getMap, A::getMap)
        .andSkipWhenNull()
        .ensure()).isInstanceOf(AssertionError.class)
        .hasMessage("The mapper was expected to create no implicit mappings but the actual mapper does.");
  }

}
