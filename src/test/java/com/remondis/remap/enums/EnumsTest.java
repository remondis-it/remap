package com.remondis.remap.enums;

import static com.remondis.remap.enums.Gender.MALE;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;
import org.junit.jupiter.api.Test;

class EnumsTest {

  @Test
  void shouldMapEnums() {
    Mapper<Person, PersonResource> mapper = Mapping.from(Person.class)
        .to(PersonResource.class)
        .mapper();

    String forename = "Armin";
    String name = "Loaf";
    Gender gender = MALE;
    Person person = new Person(forename, name, gender);
    PersonResource pr = mapper.map(person);

    assertEquals(forename, pr.getForename());
    assertEquals(name, pr.getName());
    assertEquals(gender, pr.getGender());
  }

  @Test
  void shouldThrowMappingException() {
    assertThatThrownBy(() -> Mapping.from(Person.class)
        .to(AnotherResource.class)
        .mapper()).isInstanceOf(MappingException.class)
        .hasMessageStartingWith("No mapper found for type mapping from ");
  }

}
