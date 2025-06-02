package com.remondis.remap.maps.incompatibleListMapTypeValidation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;

public class TypeValidationTest {

  @Test
  public void shouldDetectIncompatibleCollections() {
    assertThatThrownBy(() -> {
      Mapping.from(A.class)
          .to(AMapped.class)
          .mapper();
    }).isInstanceOf(MappingException.class)
        .hasMessageContaining("java.util.Set<com.remondis.remap.maps.incompatibleListMapTypeValidation.B>\n\tto"
            + "\n\tjava.util.Map<com.remondis.remap.maps.incompatibleListMapTypeValidation.BMapped, com.remondis.remap.maps.incompatibleListMapTypeValidation.CMapped>");
  }

}
