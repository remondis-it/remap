package com.remondis.remap.fluent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

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

  @Test(expected = MappingException.class)
  public void checkConfigurationOfChainedSetters() {
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
