package com.remondis.remap.writeNull;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingConfiguration;

public class WriteNullTest {

  @Test
  public void shouldWriteNull() {
    Mapper<Source, Destination> mapper = Mapping.from(Source.class)
        .to(Destination.class)
        .writeNullIfSourceIsNull()
        .mapper();

    Source source = new Source(null);

    Destination destination = new Destination("string");
    assertNotNull(destination.getString());

    Destination destinationAfter = mapper.map(source, destination);
    assertSame(destination, destinationAfter);
    assertNull(destination.getString());

    AssertMapping.of(mapper)
        .expectToWriteNullIfSourceIsNull()
        .ensure();
  }

  @Test
  public void shouldComplainAboutWrongNullHandling() {
    MappingConfiguration<Source, Destination> configuration = Mapping.from(Source.class)
        .to(Destination.class);
    Mapper<Source, Destination> mapper = configuration.mapper();

    assertFalse(configuration.isWriteNull());
    assertThatThrownBy(() -> AssertMapping.of(mapper)
        .expectToWriteNullIfSourceIsNull()
        .ensure()).isInstanceOf(AssertionError.class)
            .hasMessageContaining(
                "The mapper was expected to write null values if the source value is null, but the current mapper is configured to skip mappings if source value is null (Mapping strategy).");
  }

}
