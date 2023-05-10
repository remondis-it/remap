package com.remondis.remap.regression.npeOnUnmappedReadOnlyProperty;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.remondis.remap.Mapping;
import org.junit.jupiter.api.Test;

class MapperTest {
  @Test
  void test() {
    assertThatThrownBy(() -> Mapping.from(A.class)
        .to(B.class)
        .mapper()).hasMessageStartingWith("The following properties are unmapped:");
  }
}
