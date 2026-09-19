package com.remondis.remap.nullness;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.NullnessPolicy;

/**
 * Tests the assert API for the nullness validation. Every mapping feature of ReMap has a corresponding assert feature
 * so that a mapping specification can be pinned down by a unit test.
 */
public class NullnessAssertTest {

  @Test
  public void shouldAssertTheConfiguredPolicy() {
    Mapper<NonNullTextBean, NonNullTextBean> mapper = Mapping.from(NonNullTextBean.class)
        .to(NonNullTextBean.class)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper();

    assertThatCode(() -> AssertMapping.of(mapper)
        .expectNullnessPolicy(NullnessPolicy.ERROR)
        .ensure()).doesNotThrowAnyException();
  }

  /**
   * A mapper that does not configure a policy uses the default policy, so asserting it is not required.
   */
  @Test
  public void shouldNotRequireAnAssertionForTheDefaultPolicy() {
    Mapper<NonNullTextBean, NonNullTextBean> mapper = Mapping.from(NonNullTextBean.class)
        .to(NonNullTextBean.class)
        .mapper();

    assertThatCode(() -> AssertMapping.of(mapper)
        .ensure()).doesNotThrowAnyException();
  }

  @Test
  public void shouldComplainAboutAnUnexpectedPolicy() {
    Mapper<NonNullTextBean, NonNullTextBean> mapper = Mapping.from(NonNullTextBean.class)
        .to(NonNullTextBean.class)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper();

    assertThatThrownBy(() -> AssertMapping.of(mapper)
        .ensure()).isInstanceOf(AssertionError.class)
        .hasMessageContaining("policy OFF")
        .hasMessageContaining("policy ERROR");
  }

  @Test
  public void shouldComplainAboutAMissingPolicy() {
    Mapper<NonNullTextBean, NonNullTextBean> mapper = Mapping.from(NonNullTextBean.class)
        .to(NonNullTextBean.class)
        .mapper();

    assertThatThrownBy(() -> AssertMapping.of(mapper)
        .expectNullnessPolicy(NullnessPolicy.WARN)
        .ensure()).isInstanceOf(AssertionError.class)
        .hasMessageContaining("policy WARN")
        .hasMessageContaining("policy OFF");
  }

}
