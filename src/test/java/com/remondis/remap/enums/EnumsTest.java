package com.remondis.remap.enums;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;

public class EnumsTest {

  @Test
  public void shouldMapEnums() {
    Mapper<Person, PersonResource> mapper = Mapping.from(Person.class)
        .to(PersonResource.class)
        .mapper();

    String forename = "Armin";
    String name = "Loaf";
    Gender gender = Gender.MALE;
    Person person = new Person(forename, name, gender);
    PersonResource pr = mapper.map(person);

    assertEquals(forename, pr.getForename());
    assertEquals(name, pr.getName());
    assertEquals(gender, pr.getGender());
  }

  @Test
  public void shouldThrowMappingException() {
    assertThatThrownBy(() -> Mapping.from(Person.class)
        .to(AnotherResource.class)
        .mapper()).isInstanceOf(MappingException.class)
            .hasMessageStartingWith("No mapper found for type mapping from ");
  }

}
