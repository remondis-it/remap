package com.remondis.remap.restructure.demo;

import org.junit.jupiter.api.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.resample.Samples;

public class RestructuringDemoTest {

  @Test
  public void shouldRestructurePerson() {
    Mapper<PersonFlat, Family> mapper = Mapping.from(PersonFlat.class)
        .to(Family.class)
        .omitOtherSourceProperties()
        .restructure(Family::getDad)
        .applying(flat2PersonMapping -> flat2PersonMapping.restructure(Person::getAddress)
            .implicitly())
        .mapper();

    PersonFlat personFlat = Samples.Default.of(PersonFlat.class)
        .get();
    Family family = mapper.map(personFlat);
    System.out.println(family);

    AssertMapping.of(mapper)
        .expectOtherSourceFieldsToBeOmitted()
        .expectRestructure(Family::getDad)
        .applying(flat2PersonMapping -> flat2PersonMapping.expectRestructure(Person::getAddress)
            .implicitly())
        .ensure();
  }
}
