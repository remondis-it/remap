package com.remondis.remap.writeNull;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingConfiguration;
import com.remondis.remap.MappingStrategy;
import com.remondis.remap.ReMapDefaults;

public class MappingStrategyTest {

  private static final MappingStrategy defaultMappingStrategy = ReMapDefaults.getMappingStrategy();

  @Before
  public void resetDefault() {
    ReMapDefaults.setMappingStrategy(defaultMappingStrategy);
  }

  @Test
  public void shouldPatchByDefault() {
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .mapper();
    Source source = new Source(null);
    String destinationValue = "string";
    Destination destination = new Destination(destinationValue);

    Destination destinationAfter = mapper.map(source, destination);

    assertSame(destination, destinationAfter);
    assertEquals(destinationValue, destinationAfter.getString());
    AssertMapping.of(mapper)
        .expectMappingStrategy(MappingStrategy.PATCH)
        .ensure();
  }

  @Test
  public void shouldPutAfterOverwrite() {
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .mappingStrategy(MappingStrategy.PUT)
        .mapper();
    Source source = new Source(null);
    Destination destination = new Destination("string");

    Destination destinationAfter = mapper.map(source, destination);

    assertSame(destination, destinationAfter);
    assertNull(destinationAfter.getString());
    AssertMapping.of(mapper)
        .expectMappingStrategy(MappingStrategy.PUT)
        .ensure();
  }

  @Test
  public void shouldComplainAboutWrongMappingStrategy() {
    MappingConfiguration<Source, Destination> configuration = Mapping.from(Source.class)
        .to(Destination.class);
    Mapper<Source, Destination> mapper = configuration.mapper();

    assertFalse(configuration.isWriteNull());
    assertThatThrownBy(() -> AssertMapping.of(mapper)
        .expectMappingStrategy(MappingStrategy.PUT)
        .ensure()).isInstanceOf(AssertionError.class)
            .hasMessageContaining(
                "The mapper was expected to write null values if the source value is null, but the current mapper is configured to skip mappings if source value is null (Mapping strategy).");
  }

  @Test
  public void shouldPutByDefault() {
    ReMapDefaults.setMappingStrategy(MappingStrategy.PUT);
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .mapper();
    Source source = new Source(null);
    Destination destination = new Destination("string");

    Destination destinationAfter = mapper.map(source, destination);

    assertSame(destination, destinationAfter);
    assertNull(destinationAfter.getString());
    AssertMapping.of(mapper)
        .expectMappingStrategy(MappingStrategy.PUT)
        .ensure();
  }

  @Test
  public void shouldPatchAfterOverwrite() {
    ReMapDefaults.setMappingStrategy(MappingStrategy.PUT);
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .mappingStrategy(MappingStrategy.PATCH)
        .mapper();
    Source source = new Source(null);
    String destinationValue = "string";
    Destination destination = new Destination(destinationValue);

    Destination destinationAfter = mapper.map(source, destination);

    assertSame(destination, destinationAfter);
    assertEquals(destinationValue, destinationAfter.getString());
    AssertMapping.of(mapper)
        .expectMappingStrategy(MappingStrategy.PATCH)
        .ensure();
  }

  @Test
  public void shouldNotUseDefaultAfterMapperCreation() {
    Mapper<Source, Destination> mapper1 = Mapping.from(Source.class)
        .to(Destination.class)
        .mapper();
    ReMapDefaults.setMappingStrategy(MappingStrategy.PUT);
    Mapper<Source, Destination> mapper2 = Mapping.from(Source.class)
        .to(Destination.class)
        .mapper();

    AssertMapping.of(mapper1)
        .expectMappingStrategy(MappingStrategy.PATCH)
        .ensure();
    AssertMapping.of(mapper2)
        .expectMappingStrategy(MappingStrategy.PUT)
        .ensure();
  }
}
