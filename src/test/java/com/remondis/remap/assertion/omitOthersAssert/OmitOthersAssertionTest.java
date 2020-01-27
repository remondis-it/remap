package com.remondis.remap.assertion.omitOthersAssert;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class OmitOthersAssertionTest {

  @Test
  public void shouldComplainAboutUnexpectedOmitsForDestination() {
    Mapper<BeanEmpty, BeanWithFields> mapper = Mapping.from(BeanEmpty.class)
        .to(BeanWithFields.class)
        .omitOtherDestinationProperties()
        .mapper();

    assertThatThrownBy(() -> AssertMapping.of(mapper)
        .ensure()).hasMessageContaining("The following unexpected transformation were specified on the mapping:\n")
            .hasMessageContaining("- Omitting Property 'number' in BeanWithFields\n")
            .hasMessageContaining("- Omitting Property 'string' in BeanWithFields\n");
  }

  @Test
  public void shouldComplainAboutUnexpectedOmitsForSource() {
    Mapper<BeanWithFields, BeanEmpty> mapper = Mapping.from(BeanWithFields.class)
        .to(BeanEmpty.class)
        .omitOtherSourceProperties()
        .mapper();

    assertThatThrownBy(() -> AssertMapping.of(mapper)
        .ensure()).hasMessageContaining("The following unexpected transformation were specified on the mapping:\n")
            .hasMessageContaining("- Omitting Property 'number' in BeanWithFields\n")
            .hasMessageContaining("- Omitting Property 'string' in BeanWithFields\n");
  }

}
