package com.remondis.remap.regression.booleanObjectBug;

import com.remondis.remap.Mapping;
import org.junit.jupiter.api.Test;

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
