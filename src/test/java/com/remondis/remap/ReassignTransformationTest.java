package com.remondis.remap;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ReassignTransformationTest {

  @Test
  public void shouldReference() {
    // assertTrue(ReassignTransformation.isReferenceMapping(int.class, int.class));
    assertTrue(ReassignTransformation.isReferenceMapping(Object.class, Object.class));
  }

}
