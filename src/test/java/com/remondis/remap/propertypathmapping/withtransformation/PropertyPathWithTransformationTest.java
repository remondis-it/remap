package com.remondis.remap.propertypathmapping.withtransformation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.propertypathmapping.Address;
import com.remondis.remap.propertypathmapping.Person;

public class PropertyPathWithTransformationTest {

  private Mapper<Person, PersonView> mapper;

  @BeforeEach
  public void setup() {
    this.mapper = Mapping.from(Person.class)
        .to(PersonView.class)
        .replace(Person::getAddress, PersonView::getStreetLength)
        .withPropertyPathAnd(address -> address.getStreet())
        .apply(String::length)
        .replace(Person::getAddress, PersonView::getStreetLengthWrap)
        .withPropertyPathAnd(address -> address.getStreet())
        .apply(String::length)
        .omitOtherSourceProperties()
        .mapper();
  }

  @Test
  public void shouldNotComplainAboutNull_1() {
    Person person = new Person("forename", "name", null);
    PersonView view = mapper.map(person);
    assertNull(view.getStreetLengthWrap());
    assertEquals(0, view.getStreetLength());
  }

  @Test
  public void shouldNotComplainAboutNull_2() {
    Person person = new Person("forename", "name", new Address(null, "houseNumber", "zipCode", "city"));
    PersonView view = mapper.map(person);
    assertNull(view.getStreetLengthWrap());
    assertEquals(0, view.getStreetLength());
  }

  @Test
  public void shouldEvaluatePropertyPath() {

    String expectedStreet = "street";
    Person person = new Person("forename", "name", new Address(expectedStreet, "houseNumber", "zipCode", "city"));

    PersonView view = mapper.map(person);

    assertEquals(expectedStreet.length(), view.getStreetLength());
    assertEquals(expectedStreet.length(), (int) view.getStreetLengthWrap());
  }

  @Test
  public void shouldAssertCorrectly() {
    AssertMapping.of(mapper)
        .expectReplace(Person::getAddress, PersonView::getStreetLength)
        .withPropertyPathAndTransformation(address -> address.getStreet())
        .expectReplace(Person::getAddress, PersonView::getStreetLengthWrap)
        .withPropertyPathAndTransformation(address -> address.getStreet())
        .expectOtherSourceFieldsToBeOmitted()
        .ensure();
  }

  @Test
  public void shouldComplainAboutWrongPropertyPathCorrectly() {
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReplace(Person::getAddress, PersonView::getStreetLength)
          .withPropertyPathAndTransformation(address -> address.getHouseNumber())
          .expectReplace(Person::getAddress, PersonView::getStreetLengthWrap)
          .withPropertyPathAndTransformation(address -> address.getCity())
          .expectOtherSourceFieldsToBeOmitted()
          .ensure();
    }).isInstanceOf(AssertionError.class);
  }

  @Test
  public void shouldDistinctBetweenOtherReplaceOperations_andSkipWhenNull() {
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReplace(Person::getAddress, PersonView::getStreetLength)
          .andSkipWhenNull()
          .expectReplace(Person::getAddress, PersonView::getStreetLengthWrap)
          .andSkipWhenNull()
          .expectOtherSourceFieldsToBeOmitted()
          .ensure();
    }).isInstanceOf(AssertionError.class);
  }

  @Test
  public void shouldDistinctBetweenOtherReplaceOperations_andTest() {
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReplace(Person::getAddress, PersonView::getStreetLength)
          .andTest(add -> 1)
          .expectReplace(Person::getAddress, PersonView::getStreetLengthWrap)
          .andTest(add -> 2)
          .expectOtherSourceFieldsToBeOmitted()
          .ensure();
    }).isInstanceOf(AssertionError.class);
  }

}
