package com.remondis.remap.regression.useMapperTwice;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;

public class UseMapperTwiceTest {

  @Test
  public void should_complain_with_types_of_mapper_when_useMapperTwice() {
    Mapper<B, BMapped> bMapper = Mapping.from(B.class)
        .to(BMapped.class)
        .replace(B::getString, BMapped::getStringLength)
        .withSkipWhenNull(String::length)
        .mapper();

    assertThatThrownBy(() -> {
      Mapping.from(A.class)
          .to(AMapped.class)
          .useMapper(bMapper)
          .useMapper(bMapper);
    }).isInstanceOf(MappingException.class)
        .hasMessage(
            "A mapper mapping the type com.remondis.remap.regression.useMapperTwice.B to type com.remondis.remap.regression.useMapperTwice.BMapped was already registered.");
  }

}
