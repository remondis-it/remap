package com.remondis.remap.maps.incompatibleListMapTypeValidation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;
import org.junit.jupiter.api.Test;

class TypeValidationTest {

  @Test
  void shouldDetectIncompatibleCollections() {
    assertThatThrownBy(() -> Mapping.from(A.class)
        .to(AMapped.class)
        .mapper()).isInstanceOf(MappingException.class)
        .hasMessageContaining("java.util.Set<com.remondis.remap.maps.incompatibleListMapTypeValidation.B>\n\tto"
            + "\n\tjava.util.Map<com.remondis.remap.maps.incompatibleListMapTypeValidation.BMapped, com.remondis.remap.maps.incompatibleListMapTypeValidation.CMapped>");
  }

}
