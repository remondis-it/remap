package com.remondis.remap.regression.npeOnUnmappedReadOnlyProperty;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.remondis.remap.Mapping;

public class MapperTest {
  @Test
  public void test() {
    assertThatThrownBy(() -> {
      Mapping.from(A.class)
          .to(B.class)
          .mapper();
    }).hasMessageStartingWith("The following properties are unmapped:");
  }
}
