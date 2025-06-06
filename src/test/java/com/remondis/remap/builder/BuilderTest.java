package com.remondis.remap.builder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;

public class BuilderTest {

  private static final long IDENTIFIER = 4L;
  private static final String NAME = "Bob";

  @Test
  public void shouldMapToDestinationFromBuilder() {

    Mapper<BuilderModel, DestinationModel> mapper = Mapping.from(BuilderModel.class)
        .to(DestinationModel.class)
        .mapper();

    BuilderModel builderModel = new BuilderModel.BuilderModelBuilder(NAME).field(IDENTIFIER)
        .build();

    DestinationModel destinationModel = mapper.map(builderModel);

    assertEquals(NAME, destinationModel.getName());
    assertEquals(IDENTIFIER, (long) destinationModel.getField());

    AssertMapping.of(mapper)
        .ensure();
  }

  @Test
  public void failsWhenMapFromBuilder() {
    assertThatThrownBy(() -> {
      Mapping.from(DestinationModel.class)
          .to(BuilderModel.class)
          .mapper();
    }).isInstanceOf(MappingException.class);
  }
}
