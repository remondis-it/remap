package com.remondis.remap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This is the test of class {@link Mapper}.
 */
class MapperTest {

  private Mapper<StringDto, StringDto> mapper;

  @BeforeEach
  public void setup() {
    this.mapper = Mapping.from(StringDto.class)
        .to(StringDto.class)
        .mapper();
  }

  @Test
  void shouldMapEmptyList() {
    List<StringDto> list = mapper.map(Collections.emptyList());
    assertTrue(list.isEmpty());
  }

  @Test
  void shouldMapEmptySet() {
    Set<StringDto> list = mapper.map(Collections.emptySet());
    assertTrue(list.isEmpty());
  }

  @Test
  void shouldMapNull() {
    StringDto returnValue = mapper.mapOptional(null);
    assertNull(returnValue);
  }

  @Test
  void shouldMapOptional() {
    String expectedString = "string";
    StringDto returnValue = mapper.mapOptional(new StringDto(expectedString));
    assertNotNull(returnValue);
    assertEquals(expectedString, returnValue.getString());
  }

  @Test
  void shouldMapDefault() {
    String expectedString = "string";
    StringDto expectedA = new StringDto(expectedString);
    StringDto returnValue = mapper.mapOrDefault(null, expectedA);
    assertNotNull(returnValue);
    assertSame(expectedA, returnValue);
  }

  @Test
  void shouldNotMapDefault() {
    String expectedString = "string";
    StringDto expectedA = new StringDto(expectedString);
    StringDto notExpectedDefault = new StringDto("someOtherString");
    StringDto returnValue = mapper.mapOrDefault(expectedA, notExpectedDefault);
    assertNotNull(returnValue);
    assertEquals(expectedA, returnValue);
  }

}
