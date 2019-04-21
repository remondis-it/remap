package com.remondis.remap.propertypathmapping;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class PropertyPathTest {

  private Mapper<Person, PersonView> mapper;

  @Before
  public void setup() {
    this.mapper = Mapping.from(Person.class)
        .to(PersonView.class)
        .replace(Person::getAddress, PersonView::getStreet)
        .withPropertyPath(Address::getStreet)
        .replace(Person::getAddress, PersonView::getHouseNumber)
        .withPropertyPath(Address::getHouseNumber)
        .replace(Person::getAddress, PersonView::getZipCode)
        .withPropertyPath(Address::getZipCode)
        .replace(Person::getAddress, PersonView::getCity)
        .withPropertyPath(Address::getCity)
        .mapper();
  }

  @Test
  public void shouldNotComplainAboutNull_1() {
    Person person = new Person("forename", "name", null);
    PersonView view = mapper.map(person);
    assertEquals(person.getForename(), view.getForename());
    assertEquals(person.getName(), view.getName());
    assertNull(person.getAddress());
  }

  @Test
  public void shouldNotComplainAboutNull_2() {
    Person person = new Person("forename", "name", new Address(null, "houseNumber", "zipCode", "city"));
    PersonView view = mapper.map(person);
    assertEquals(person.getForename(), view.getForename());
    assertEquals(person.getName(), view.getName());
    assertNull(person.getAddress()
        .getStreet());
    assertEquals(person.getAddress()
        .getHouseNumber(), view.getHouseNumber());
    assertEquals(person.getAddress()
        .getZipCode(), view.getZipCode());
    assertEquals(person.getAddress()
        .getCity(), view.getCity());
  }

  @Test
  public void shouldEvaluatePropertyPath() {

    Person person = new Person("forename", "name", new Address("street", "houseNumber", "zipCode", "city"));

    PersonView view = mapper.map(person);

    assertEquals(person.getForename(), view.getForename());
    assertEquals(person.getName(), view.getName());
    assertEquals(person.getAddress()
        .getStreet(), view.getStreet());
    assertEquals(person.getAddress()
        .getHouseNumber(), view.getHouseNumber());
    assertEquals(person.getAddress()
        .getZipCode(), view.getZipCode());
    assertEquals(person.getAddress()
        .getCity(), view.getCity());

    AssertMapping.of(mapper)
        .expectReplace(Person::getAddress, PersonView::getStreet)
        .withPropertyPath(Address::getStreet)
        .expectReplace(Person::getAddress, PersonView::getHouseNumber)
        .withPropertyPath(Address::getHouseNumber)
        .expectReplace(Person::getAddress, PersonView::getZipCode)
        .withPropertyPath(Address::getZipCode)
        .expectReplace(Person::getAddress, PersonView::getCity)
        .withPropertyPath(Address::getCity)
        .ensure();

  }

  @Test
  public void shouldDistinctBetweenOtherReplaceOperations_andSkipWhenNull() {
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReplace(Person::getAddress, PersonView::getStreet)
          .andSkipWhenNull()
          .expectReplace(Person::getAddress, PersonView::getHouseNumber)
          .andSkipWhenNull()
          .expectReplace(Person::getAddress, PersonView::getZipCode)
          .andSkipWhenNull()
          .expectReplace(Person::getAddress, PersonView::getCity)
          .andSkipWhenNull()
          .ensure();
    }).isInstanceOf(AssertionError.class);
  }

  @Test
  public void shouldDistinctBetweenOtherReplaceOperations_andTest() {
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReplace(Person::getAddress, PersonView::getStreet)
          .andTest(add -> "string")
          .expectReplace(Person::getAddress, PersonView::getHouseNumber)
          .andTest(add -> "string")
          .expectReplace(Person::getAddress, PersonView::getZipCode)
          .andTest(add -> "string")
          .expectReplace(Person::getAddress, PersonView::getCity)
          .andTest(add -> "string")
          .ensure();
    }).isInstanceOf(AssertionError.class);
  }

}
