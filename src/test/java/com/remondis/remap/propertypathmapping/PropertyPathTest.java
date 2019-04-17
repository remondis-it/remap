package com.remondis.remap.propertypathmapping;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

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
        .byPropertyPath(Address::getStreet)
        .replace(Person::getAddress, PersonView::getHouseNumber)
        .byPropertyPath(Address::getHouseNumber)
        .replace(Person::getAddress, PersonView::getZipCode)
        .byPropertyPath(Address::getZipCode)
        .replace(Person::getAddress, PersonView::getCity)
        .byPropertyPath(Address::getCity)
        .mapper();
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
        .byPropertyPath(Address::getStreet)
        .expectReplace(Person::getAddress, PersonView::getHouseNumber)
        .byPropertyPath(Address::getHouseNumber)
        .expectReplace(Person::getAddress, PersonView::getZipCode)
        .byPropertyPath(Address::getZipCode)
        .expectReplace(Person::getAddress, PersonView::getCity)
        .byPropertyPath(Address::getCity)
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
