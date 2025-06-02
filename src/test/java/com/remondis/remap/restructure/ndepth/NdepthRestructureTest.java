package com.remondis.remap.restructure.ndepth;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingModel;
import com.remondis.remap.restructure.Address;
import com.remondis.remap.restructure.Bean;
import com.remondis.resample.Samples;

public class NdepthRestructureTest {

  @Test
  public void shouldReturnMappingModel_n_depth_for_Restructure_n_depth() {

    Mapper<Bean, Bean2> mapper = createMapper();
    MappingModel<Bean, Bean2>.TransformationSearchResult result = mapper.getMappingModel()
        .findMappingBySource(MappingModel.nameEqualsPredicateIgnoreCase("city"));
    System.out.println(result);
  }

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
        .ensure()).hasMessage("The mapping from source type com.remondis.remap.restructure.Bean\n"
            + "used for restructuring of field Property 'person' in com.remondis.remap.restructure.ndepth.Bean2\n"
            + "was configured to apply specific mapping configuration but was expected to create implicit mapping.");
  }

  @Test
  public void shouldCompainAboutDifferentMappings() {
    Mapper<Bean, Bean2> mapper = Mapping.from(Bean.class)
        .to(Bean2.class)
        .omitOtherSourceProperties()
        .restructure(Bean2::getPerson)
        .applying(config -> config.replace(Bean::getHouseNumber, Person::getName)
            .withSkipWhenNull(number -> String.valueOf(number + 100))
            .set(Person::getForename)
            .with("AnotherString")
            .omitOthers())
        .mapper();

    assertThatThrownBy(() -> AssertMapping.of(mapper)
        .expectOtherSourceFieldsToBeOmitted()
        .expectRestructure(Bean2::getPerson)
        .applying(config -> config.expectRestructure(Person::getAddress)
            .applying(bean2AddressMapper -> bean2AddressMapper.expectReassign(Bean::getCity)
                .to(Address::getCity)))
        .ensure()).hasMessage("The mapping from source type com.remondis.remap.restructure.Bean\n"
            + "used for restructuring of field in Property 'person' in com.remondis.remap.restructure.ndepth.Bean2\n"
            + "did not meet assertions:\n"
            + "The following expected transformation were not specified on the mapping:\n"
            + "- Restructure complex object for field Property 'address' in Person.\n");
  }

  @Test
  public void shouldComplainAboutUnexpectedNestedMappingConfiguration() {
    assertThatThrownBy(() -> AssertMapping.of(createMapper())
        .expectOtherSourceFieldsToBeOmitted()
        .expectRestructure(Bean2::getPerson)
        .applying(config -> config.expectRestructure(Person::getAddress)
            .applying(bean2AddressMapper -> bean2AddressMapper.expectReassign(Bean::getCity)
                .to(Address::getCity)))
        .ensure()).hasMessage("The mapping from source type com.remondis.remap.restructure.Bean\n"
            + "used for restructuring of field in Property 'person' in com.remondis.remap.restructure.ndepth.Bean2\n"
            + "did not meet assertions:\n" + "The mapping from source type com.remondis.remap.restructure.Bean\n"
            + "used for restructuring of field Property 'address' in com.remondis.remap.restructure.ndepth.Person\n"
            + "was configured to create implicit mapping but was expected to apply specific mapping configuration.");
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
