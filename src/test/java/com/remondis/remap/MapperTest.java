package com.remondis.remap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

/**
 * This is the test of class {@link Mapper}.
 */
public class MapperTest {

  private Mapper<StringDto, StringDto> mapper;

  @Before
  public void setup() {
    this.mapper = Mapping.from(StringDto.class)
        .to(StringDto.class)
        .mapper();
  }

  @Test
  public void shouldMapEmptyList() {
    List<StringDto> list = mapper.map(Collections.emptyList());
    assertTrue(list.isEmpty());
  }

  @Test
  public void shouldMapEmptySet() {
    Set<StringDto> list = mapper.map(Collections.emptySet());
    assertTrue(list.isEmpty());
  }

  @Test
  public void shouldMapNull() {
    StringDto returnValue = mapper.mapOptional(null);
    assertNull(returnValue);
  }

  @Test
  public void shouldMapOptional() {
    String expectedString = "string";
    StringDto returnValue = mapper.mapOptional(new StringDto(expectedString));
    assertNotNull(returnValue);
    assertEquals(expectedString, returnValue.getString());
  }

  @Test
  public void shouldMapDefault() {
    String expectedString = "string";
    StringDto expectedA = new StringDto(expectedString);
    StringDto returnValue = mapper.mapOrDefault(null, expectedA);
    assertNotNull(returnValue);
    assertSame(expectedA, returnValue);
  }

  @Test
  public void shouldNotMapDefault() {
    String expectedString = "string";
    StringDto expectedA = new StringDto(expectedString);
    StringDto notExpectedDefault = new StringDto("someOtherString");
    StringDto returnValue = mapper.mapOrDefault(expectedA, notExpectedDefault);
    assertNotNull(returnValue);
    assertEquals(expectedA, returnValue);
  }

}
