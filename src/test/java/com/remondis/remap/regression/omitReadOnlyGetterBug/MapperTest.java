package com.remondis.remap.regression.omitReadOnlyGetterBug;

import com.remondis.remap.Mapping;
import org.junit.jupiter.api.Test;

class MapperTest {
  @Test
  void test() {

    Mapping.from(A.class)
        .to(B.class)
        .omitInSource(A::getReadOnly)
        .mapper();

  }
}
