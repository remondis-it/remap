package com.remondis.remap.regression.booleanObjectBug;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapping;

class MapperTest {

  @Test
  void shouldMap() {
    Mapping.from(A.class)
        .to(B.class)
        .reassign(A::getMail)
        .to(B::getEmail)
        .omitInSource(A::getNewsletterSubscribed)
        .mapper();
  }
}
