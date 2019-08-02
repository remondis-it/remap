package com.remondis.remap.maps.listMapTypeValidation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;

public class TypeValidationTest {

  @Test
  public void shouldDetectBothNestedGenericTypes() {

    assertThatThrownBy(() -> {
      Mapping.from(A.class)
          .to(AMapped.class)
          .mapper();
    }).isInstanceOf(MappingException.class)
        .hasMessageContaining(
            "No mapper found for type mapping from com.remondis.remap.maps.listMapTypeValidation.B to com.remondis.remap.maps.listMapTypeValidation.BMapped.");
  }
}
