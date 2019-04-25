package com.remondis.remap.propertypathmapping.withtransformation.collections;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.propertypathmapping.Address;

public class PropertyPathWithTransformationCollectionTest {

  private Mapper<Person, PersonView> mapper;

  @Before
  public void setup() {
    this.mapper = Mapping.from(Person.class)
        .to(PersonView.class)
        .replaceCollection(Person::getAddresses, PersonView::getStreetLength)
        .withPropertyPathAnd(add -> add.getStreet())
        .apply(Integer::valueOf)
        .mapper();
  }

  @Test
  public void shouldNotComplainAboutNull_1() {
    Person person = new Person();
    PersonView view = mapper.map(person);
    assertNull(view.getStreetLength());
  }

  @Test
  public void shouldNotComplainAboutNull_2() {
    Person person = new Person(asList(new Address(null, "houseNumber", "zipCode", "city")));
    PersonView view = mapper.map(person);
    assertNotNull(view.getStreetLength());
    assertTrue(view.getStreetLength()
        .isEmpty());
  }

  @Test
  public void shouldEvaluatePropertyPath() {

    String expectedStreet1 = "1";
    String expectedStreet2 = "2";
    String expectedStreet3 = "3";
    Person person = new Person(asList(new Address(expectedStreet1, "houseNumber", "zipCode", "city"),
        new Address(expectedStreet2, "houseNumber", "zipCode", "city"),
        new Address(expectedStreet3, "houseNumber", "zipCode", "city")));

    PersonView view = mapper.map(person);

    assertThat(view.getStreetLength()).containsExactlyInAnyOrder(1, 2, 3);
  }

  @Test
  public void shouldAssertCorrectly() {
    AssertMapping.of(mapper)
        .expectReplaceCollection(Person::getAddresses, PersonView::getStreetLength)
        .withPropertyPathAndTransformation(add -> add.getStreet())
        .ensure();
  }

  @Test
  public void shouldComplainAboutWrongPropertyPathCorrectly() {
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReplaceCollection(Person::getAddresses, PersonView::getStreetLength)
          .withPropertyPathAndTransformation(address -> address.getHouseNumber())
          .ensure();
    }).isInstanceOf(AssertionError.class);
  }

  @Test
  public void shouldDistinctBetweenOtherReplaceOperations_andSkipWhenNull() {
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReplaceCollection(Person::getAddresses, PersonView::getStreetLength)
          .andSkipWhenNull()
          .ensure();
    }).isInstanceOf(AssertionError.class);
  }

  @Test
  public void shouldDistinctBetweenOtherReplaceOperations_andTest() {
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReplaceCollection(Person::getAddresses, PersonView::getStreetLength)
          .andTest(add -> 1)
          .ensure();
    }).isInstanceOf(AssertionError.class);
  }

}
