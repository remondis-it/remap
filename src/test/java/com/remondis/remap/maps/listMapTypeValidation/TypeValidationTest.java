package com.remondis.remap.maps.listMapTypeValidation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;

public class TypeValidationTest {

  @Test
  public void shouldDetectBothNestedGenericTypes() {
    Mapper<B, BMapped> bMapper = Mapping.from(B.class)
        .to(BMapped.class)
        .mapper();

    Mapper<C, CMapped> cMapper = Mapping.from(C.class)
        .to(CMapped.class)
        .mapper();

    assertThatThrownBy(() -> {
      Mapping.from(A.class)
          .to(AMapped.class)
          .useMapper(cMapper)
          .mapper();
    }).isInstanceOf(MappingException.class)
        .hasMessageContaining(
            "No mapper found for type mapping from com.remondis.remap.maps.listMapTypeValidation.B to com.remondis.remap.maps.listMapTypeValidation.BMapped.");

    assertThatThrownBy(() -> {
      Mapping.from(A.class)
          .to(AMapped.class)
          .useMapper(bMapper)
          .mapper();
    }).isInstanceOf(MappingException.class)
        .hasMessageContaining(
            "No mapper found for type mapping from com.remondis.remap.maps.listMapTypeValidation.C to com.remondis.remap.maps.listMapTypeValidation.CMapped.");

  }
}
