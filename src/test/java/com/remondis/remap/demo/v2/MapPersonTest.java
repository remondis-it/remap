package com.remondis.remap.demo.v2;

import static java.time.LocalDate.now;
import static java.time.Period.between;

import java.time.LocalDate;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MapPersonTest {

  @Test
  public void shouldMapToPersonView() {
    Person person = new Person("Mustermann", "Max", LocalDate.of(1988, 10, 9),
        new Address("Somewhere", "17a", "12346", "Nowhere", "max.mustermann@example.org"), "DE-71545498927");

    Mapper<Person, PersonView> mapper = Mapping.from(Person.class)
        .to(PersonView.class)
        // Rename a field
        .reassign(Person::getName)
        .to(PersonView::getSurname)
        // Optional field (may be null)
        .replace(Person::getBirthday, PersonView::getAge)
        .withSkipWhenNull(birthday -> between(birthday, now()).getYears())
        // Optional field (may be null)
        .replace(Person::getAddress, PersonView::getEmail)
        .withSkipWhenNull(Address::getEmail)
        // Hide tax id
        .omitInSource(Person::getTaxId)
        .mapper();

    PersonView personView = mapper.map(person);
    // Maps to: PersonView [forname=Max, surname=Mustermann, age=30, email=max.mustermann@example.org]
  }

}
