package com.remondis.remap.writeNull;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingConfiguration;
import org.junit.jupiter.api.Test;

class WriteNullTest {

  @Test
  void shouldWriteNull() {
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
  void shouldComplainAboutWrongNullHandling() {

    MappingConfiguration<Source, Destination> configuration = Mapping.from(Source.class)
        .to(Destination.class);
    Mapper<Source, Destination> mapper = configuration.mapper();

    assertFalse(configuration.isWriteNull());
    assertThatThrownBy(() -> AssertMapping.of(mapper)
        .expectToWriteNullIfSourceIsNull()
        .ensure()).isInstanceOf(AssertionError.class)
        .hasMessageContaining(
            "The mapper was expected to write null values if the source value is null, but the current mapper is configured to skip mappings if source value is null.");
  }

}
