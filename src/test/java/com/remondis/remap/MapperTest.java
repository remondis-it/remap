package com.remondis.remap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

/**
 * This is the test of class {@link Mapper}.
 */
public class MapperTest {

  private Mapper<A, A> mapper;

  @Before
  public void setup() {
    this.mapper = Mapping.from(A.class)
        .to(A.class)
        .mapper();
  }

  @Test
  public void shouldMapNull() {
    A returnValue = mapper.mapOptional(null);
    assertNull(returnValue);
  }

  @Test
  public void shouldMapOptional() {
    String expectedString = "string";
    A returnValue = mapper.mapOptional(new A(expectedString));
    assertNotNull(returnValue);
    assertEquals(expectedString, returnValue.getString());
  }

  @Test
  public void shouldMapDefault() {
    String expectedString = "string";
    A expectedA = new A(expectedString);
    A returnValue = mapper.mapOrDefault(null, expectedA);
    assertNotNull(returnValue);
    assertSame(expectedA, returnValue);
  }

  @Test
  public void shouldNotMapDefault() {
    String expectedString = "string";
    A expectedA = new A(expectedString);
    A notExpectedDefault = new A("someOtherString");
    A returnValue = mapper.mapOrDefault(expectedA, notExpectedDefault);
    assertNotNull(returnValue);
    assertEquals(expectedA, returnValue);
  }

}
