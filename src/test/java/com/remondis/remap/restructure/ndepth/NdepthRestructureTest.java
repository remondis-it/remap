package com.remondis.remap.restructure.ndepth;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.restructure.Address;
import com.remondis.remap.restructure.Bean;
import com.remondis.resample.Samples;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertSame;

public class NdepthRestructureTest {

  @Test
  public void shouldRestructure_n_depth() {

    Mapper<Bean, Bean2> mapper = createMapper();

    Bean bean = Samples.Default.of(Bean.class)
        .get();
    Bean2 bean2 = mapper.map(bean);

    assertSame(bean.getForename(), bean2.getPerson()
        .getForename());
    assertSame(bean.getName(), bean2.getPerson()
        .getName());

    assertSame(bean.getStreet(), bean2.getPerson()
        .getAddress()
        .getStreet());
    assertSame(bean.getHouseNumber(), bean2.getPerson()
        .getAddress()
        .getHouseNumber());
    assertSame(bean.getCity(), bean2.getPerson()
        .getAddress()
        .getCity());
  }

  @Test
  public void shouldAssertRestructuring() {
    AssertMapping.of(createMapper())
        .expectOtherSourceFieldsToBeOmitted()
        .expectRestructure(Bean2::getPerson)
        .applying(config -> config.expectRestructure(Person::getAddress)
            .implicitly())
        .ensure();
  }

  @Test
  public void shouldComplainAboutUnexpectedMappingConfiguration() {
    assertThatThrownBy(() -> AssertMapping.of(createMapper())
        .expectOtherSourceFieldsToBeOmitted()
        .expectRestructure(Bean2::getPerson)
        .implicitly()
        .ensure()).hasMessage(
            "Mapping from source type com.remondis.remap.restructure.Bean used for restructuring of field Property 'person' in "
                + "com.remondis.remap.restructure.ndepth.Bean2 was configured to apply specific mapping configuration but was"
                + " expected to create implicit mapping.");
  }

  @Test
  public void shouldCompainAboutDifferenMappings() {
    Mapping.from(Bean.class)
        .to(Bean2.class)
        .omitOtherSourceProperties()
        .restructure(Bean2::getPerson)
        .applying(config -> config.replace(Bean::getHouseNumber, Person::getName)
            .withSkipWhenNull(number -> String.valueOf(number + 100))
            .set(Person::getForename)
            .with("bla")
            .omitOthers())
        .mapper();
  }

  @Test
  public void shouldComplainAboutUnexpectedNestedMappingConfiguration() {
    assertThatThrownBy(() -> AssertMapping.of(createMapper())
        .expectOtherSourceFieldsToBeOmitted()
        .expectRestructure(Bean2::getPerson)
        .applying(config -> config.expectRestructure(Person::getAddress)
            .applying(bean2AddressMapper -> bean2AddressMapper.expectReassign(Bean::getCity)
                .to(Address::getCity)))
        .ensure()).hasMessage(
            "Mapping from source type com.remondis.remap.restructure.Bean used for restructuring of field in Property 'person' "
                + "in com.remondis.remap.restructure.ndepth.Bean2 did not meet assertions:\n"
                + "Mapping from source type com.remondis.remap.restructure.Bean used for restructuring of field"
                + " Property 'address' in com.remondis.remap.restructure.ndepth.Person was configured to create "
                + "implicit mapping but was expected to apply specific mapping configuration.\n");
  }

  private Mapper<Bean, Bean2> createMapper() {
    return Mapping.from(Bean.class)
        .to(Bean2.class)
        .omitOtherSourceProperties()
        .restructure(Bean2::getPerson)
        .applying(config -> config.restructure(Person::getAddress)
            .implicitly())
        .mapper();
  }

}
