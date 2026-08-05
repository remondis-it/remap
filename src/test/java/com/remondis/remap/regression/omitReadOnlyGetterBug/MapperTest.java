package com.remondis.remap.regression.omitReadOnlyGetterBug;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapping;

class MapperTest {
  @Test
  void test() {

    Mapping.from(A.class)
        .to(B.class)
        .omitInSource(A::getReadOnly)
        .mapper();

  }
}
