package com.remondis.remap.regression.booleanBug;

import org.junit.Test;

import com.remondis.remap.Mapping;

public class MapperTest {

  @Test
  public void shouldMap() {
    Mapping.from(A.class)
        .to(B.class)
        .reassign(A::getMail)
        .to(B::getEmail)
        .omitInSource(A::getNewsletterSubscribed)
        .mapper();
  }
}
