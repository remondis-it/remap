package com.remondis.remap.regression.restructureUseMapperOrderBug;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingConfiguration;

/**
 * Reproduces a configuration order bug: {@link MappingConfiguration#restructure} used to capture a snapshot of the
 * mappers registered on the enclosing configuration at the time it was called. A mapper registered afterwards with
 * {@link MappingConfiguration#useMapper} - but still before the terminal {@code mapper()} call - was silently
 * invisible to the nested restructure mapper, even though it is registered on the very same configuration instance
 * and available for every other (non-restructured) field. This turned a purely cosmetic call-order choice into a
 * "no mapper found" {@code MappingException}.
 */
class MapperTest {

  @Test
  void shouldSeeMapperRegisteredAfterRestructure() {
    Mapper<Inner, InnerDto> innerMapper = Mapping.from(Inner.class)
        .to(InnerDto.class)
        .mapper();

    MappingConfiguration<Outer, OuterDto> config = Mapping.from(Outer.class)
        .to(OuterDto.class)
        .omitOtherSourceProperties();

    // restructure() is configured first...
    config.restructure(OuterDto::getNested)
        .implicitly();

    // ...and useMapper() is only called afterwards. The nested restructure mapper still needs this mapper because
    // Outer.inner (Inner) is implicitly mapped to Nested.inner (InnerDto).
    config.useMapper(innerMapper);

    Mapper<Outer, OuterDto> mapper = config.mapper();

    Outer outer = new Outer();
    outer.setName("Bob");
    Inner inner = new Inner();
    inner.setValue("innerValue");
    outer.setInner(inner);

    OuterDto outerDto = mapper.map(outer);

    assertThat(outerDto.getNested()
        .getName()).isEqualTo("Bob");
    assertThat(outerDto.getNested()
        .getInner()
        .getValue()).isEqualTo("innerValue");
  }

  @Test
  void shouldSeeMapperRegisteredBeforeRestructure() {
    Mapper<Inner, InnerDto> innerMapper = Mapping.from(Inner.class)
        .to(InnerDto.class)
        .mapper();

    Mapper<Outer, OuterDto> mapper = Mapping.from(Outer.class)
        .to(OuterDto.class)
        .omitOtherSourceProperties()
        .useMapper(innerMapper)
        .restructure(OuterDto::getNested)
        .implicitly()
        .mapper();

    Outer outer = new Outer();
    outer.setName("Alice");
    Inner inner = new Inner();
    inner.setValue("otherValue");
    outer.setInner(inner);

    OuterDto outerDto = mapper.map(outer);

    assertThat(outerDto.getNested()
        .getName()).isEqualTo("Alice");
    assertThat(outerDto.getNested()
        .getInner()
        .getValue()).isEqualTo("otherValue");
  }
}
