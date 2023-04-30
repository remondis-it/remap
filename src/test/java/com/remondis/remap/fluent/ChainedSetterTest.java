package com.remondis.remap.fluent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingConfiguration;
import com.remondis.remap.MappingException;

class ChainedSetterTest {

  @Test
  void testChainedSetter() {
    Mapper<FluentSetterDto, FluentSetterDto> m = Mapping.from(FluentSetterDto.class)
        .to(FluentSetterDto.class)
        .allowFluentSetters()
        .mapper();
    FluentSetterDto expected = new FluentSetterDto().setInteger(5)
        .setI(22)
        .setS("str")
        .setB1(true)
        .setB2(true);
    FluentSetterDto actual = m.map(expected);
    assertThat(actual).isEqualToComparingFieldByField(expected);
  }

  @Test
  void shouldComplainAboutEnabledFluentSetters() {
    Mapper<FluentSetterDto, FluentSetterDto> mapper = Mapping.from(FluentSetterDto.class)
        .to(FluentSetterDto.class)
        .allowFluentSetters()
        .mapper();

    assertThatThrownBy(() -> AssertMapping.of(mapper)
        .ensure()).isInstanceOf(AssertionError.class)
        .hasMessageContaining(
            "The mapper was expected to only support Java Bean compliant setter methods, but the current mapper is configured to also support fluent setter methods.");
  }

  @Test
  void shouldComplainAboutDisabledFluentSetters() {
    Mapper<FluentSetterDto, FluentSetterDto> mapper = Mapping.from(FluentSetterDto.class)
        .to(FluentSetterDto.class)
        .omitOthers()
        .mapper();

    assertThatThrownBy(() -> AssertMapping.of(mapper)
        .expectOthersToBeOmitted()
        .expectFluentSettersAllowed()
        .ensure()).isInstanceOf(AssertionError.class)
        .hasMessageContaining(
            "The mapper was expected to support fluent setter methods, but the current mapper is configured to only handle Java Bean compliant setter methods.");
  }

  @Test
  void shouldTestTheMapperWithoutErrors() {
    Mapper<FluentSetterDto, FluentSetterDto> mapper = Mapping.from(FluentSetterDto.class)
        .to(FluentSetterDto.class)
        .omitOthers()
        .mapper();

    AssertMapping.of(mapper)
        .expectOthersToBeOmitted()
        .ensure();
  }

  @Test
  void shouldComplainAboutUnmappedProperties_dueToFluentSettersDisabled() {
    assertThrows(MappingException.class, () -> Mapping.from(FluentSetterDto.class)
        .to(FluentSetterDto.class)
        .mapper());
  }

  @Test
  void shouldNotComplainAboutMissingMappings_ifFluentSettersEnabled() {
    Mapping.from(FluentSetterDto.class)
        .to(FluentSetterDto.class)
        .allowFluentSetters()
        .mapper();
  }

  @Test
  void fluentSettersShouldBeDisabledByDefault_backwardsCompatibility() {
    MappingConfiguration<FluentSetterDto, FluentSetterDto> mappingConfiguration = Mapping.from(FluentSetterDto.class)
        .to(FluentSetterDto.class);
    assertFalse(mappingConfiguration.isFluentSettersAllowed());
  }
}
