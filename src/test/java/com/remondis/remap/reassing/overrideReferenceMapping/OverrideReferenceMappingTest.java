package com.remondis.remap.reassing.overrideReferenceMapping;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.TypeMapping;

public class OverrideReferenceMappingTest {

  @Test
  public void shouldUseReferenceMapping() {
    Mapper<A, AMapped> mapper = Mapping.from(A.class)
        .to(AMapped.class)
        .mapper();

    String expectedString = "test";
    long expectedNumber = 101L;

    String expectedBeforeMappingString = "test1";
    long expectedBeforeMappingNumber = 10L;

    A a = new A(expectedString, expectedNumber, new B(expectedBeforeMappingString, expectedBeforeMappingNumber));
    AMapped aMapped = mapper.map(a);

    assertEquals(expectedNumber, (long) aMapped.getNumber());
    assertEquals(expectedString, aMapped.getString());

    B b = aMapped.getB();
    assertEquals(expectedBeforeMappingString, b.getString());
    assertEquals(expectedBeforeMappingNumber, (long) b.getNumber());
  }

  @Test
  public void shouldOverrideReferenceMapping() {
    Mapper<A, AMapped> mapper = Mapping.from(A.class)
        .to(AMapped.class)
        .useMapper(TypeMapping.from(B.class)
            .to(B.class)
            .applying(b -> new B(b.getString() + "mapped", b.getNumber() + 1)))
        .mapper();

    String expectedString = "test";
    long expectedNumber = 101L;

    String expectedBeforeMappingString = "test1";
    long expectedBeforeMappingNumber = 10L;

    A a = new A(expectedString, expectedNumber, new B(expectedBeforeMappingString, expectedBeforeMappingNumber));
    AMapped aMapped = mapper.map(a);

    assertEquals(expectedNumber, (long) aMapped.getNumber());
    assertEquals(expectedString, aMapped.getString());

    B b = aMapped.getB();
    assertEquals(expectedBeforeMappingString + "mapped", b.getString());
    assertEquals(expectedBeforeMappingNumber + 1, (long) b.getNumber());
  }

}
