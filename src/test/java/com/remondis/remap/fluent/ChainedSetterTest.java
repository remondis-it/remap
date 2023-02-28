package com.remondis.remap.fluent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingConfiguration;
import com.remondis.remap.MappingException;

public class ChainedSetterTest {

  @Test
  public void testChainedSetter() {
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
  public void shouldComplainAboutEnabledFluentSetters() {
    Mapper<FluentSetterDto, FluentSetterDto> mapper = Mapping.from(FluentSetterDto.class)
        .to(FluentSetterDto.class)
        .allowFluentSetters()
        .mapper();

    Assertions.assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .ensure();
    })
        .isInstanceOf(AssertionError.class)
        .hasMessageContaining(
            "The mapper was expected to only support Java Bean compliant setter methods, but the current mapper is configured to also support fluent setter methods.");
  }

  @Test
  public void shouldComplainAboutDisabledFluentSetters() {
    Mapper<FluentSetterDto, FluentSetterDto> mapper = Mapping.from(FluentSetterDto.class)
        .to(FluentSetterDto.class)
        .omitOthers()
        .mapper();

    Assertions.assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectOthersToBeOmitted()
          .expectFluentSettersAllowed()
          .ensure();
    })
        .isInstanceOf(AssertionError.class)
        .hasMessageContaining(
            "The mapper was expected to support fluent setter methods, but the current mapper is configured to only handle Java Bean compliant setter methods.");
  }

  @Test
  public void shouldTestTheMapperWithoutErrors() {
    Mapper<FluentSetterDto, FluentSetterDto> mapper = Mapping.from(FluentSetterDto.class)
        .to(FluentSetterDto.class)
        .omitOthers()
        .mapper();

    AssertMapping.of(mapper)
        .expectOthersToBeOmitted()
        .ensure();
  }

  @Test(expected = MappingException.class)
  public void shouldComplainAboutUnmappedProperties_dueToFluentSettersDisabled() {
    Mapping.from(FluentSetterDto.class)
        .to(FluentSetterDto.class)
        .mapper();
  }

  @Test
  public void shouldNotComplainAboutMissingMappings_ifFluentSettersEnabled() {
    Mapping.from(FluentSetterDto.class)
        .to(FluentSetterDto.class)
        .allowFluentSetters()
        .mapper();
  }

  @Test
  public void fluentSettersShouldBeDisabledByDefault_backwardsCompatibility() {
    MappingConfiguration<FluentSetterDto, FluentSetterDto> mappingConfiguration = Mapping.from(FluentSetterDto.class)
        .to(FluentSetterDto.class);
    assertFalse(mappingConfiguration.isFluentSettersAllowed());
  }
}
