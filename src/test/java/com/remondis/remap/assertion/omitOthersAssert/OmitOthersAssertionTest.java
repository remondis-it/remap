package com.remondis.remap.assertion.omitOthersAssert;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class OmitOthersAssertionTest {

    @Test
    public void shouldComplainAboutUnexpectedOmitsForDestination() {
        Mapper<BeanEmpty, BeanWithFields> mapper = Mapping.from(BeanEmpty.class).to(BeanWithFields.class)
                .omitOtherDestinationProperties()
                .mapper();

        assertThatThrownBy(() -> AssertMapping.of(mapper)
                .ensure()).hasMessage("The following unexpected transformation were specified on the mapping:\n"
                + "- Omitting Property 'number' in BeanWithFields\n"
                + "- Omitting Property 'string' in BeanWithFields\n");
    }

    @Test
    public void shouldComplainAboutUnexpectedOmitsForSource() {
        Mapper<BeanWithFields, BeanEmpty> mapper = Mapping.from(BeanWithFields.class).to(BeanEmpty.class)
                .omitOtherSourceProperties()
                .mapper();

        assertThatThrownBy(() -> AssertMapping.of(mapper)
                .ensure()).hasMessage("The following unexpected transformation were specified on the mapping:\n" +
                "- Omitting Property 'number' in BeanWithFields\n" +
                "- Omitting Property 'string' in BeanWithFields\n");
    }

}
