package com.remondis.remap.propertypathmapping.collections;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.propertypathmapping.Address;
import com.remondis.remap.propertypathmapping.Person;

public class PropertyPathCollectionTest {

  private Mapper<CollectionSource, CollectionDestination> mapper;

  @BeforeEach
  public void setup() {
    this.mapper = Mapping.from(CollectionSource.class)
        .to(CollectionDestination.class)
        .replaceCollection(CollectionSource::getPersons, CollectionDestination::getCities)
        .withPropertyPath(p -> p.getAddress()
            .getCity())
        .mapper();
  }

  @Test
  public void shouldAssertCorrectly() {
    AssertMapping.of(mapper)
        .expectReplaceCollection(CollectionSource::getPersons, CollectionDestination::getCities)
        .withPropertyPath(p -> p.getAddress()
            .getCity())
        .ensure();
  }

  @Test
  public void shouldNotComplainAboutNullValue() {
    String expected1 = "city1";
    String expected2 = "city3";
    Person p1 = new Person("forename1", "name1", new Address("street1", "houseNumber1", "zipCode1", expected1));
    Person p2 = new Person("forename2", "name2", null);
    Person p3 = new Person("forename3", "name3", new Address("street3", "houseNumber3", "zipCode3", expected2));

    Set<String> expectedSet = new HashSet<>();
    expectedSet.add(expected1);
    expectedSet.add(expected2);

    CollectionSource source = new CollectionSource(asList(p1, p2, p3));

    CollectionDestination destination = mapper.map(source);

    assertEquals(expectedSet, destination.getCities());
  }

  @Test
  public void shouldNotComplainAboutNullItems() {
    String expected1 = "city1";
    String expected2 = "city3";
    Person p1 = new Person("forename1", "name1", new Address("street1", "houseNumber1", "zipCode1", expected1));
    Person p3 = new Person("forename3", "name3", new Address("street3", "houseNumber3", "zipCode3", expected2));

    Set<String> expectedSet = new HashSet<>();
    expectedSet.add(expected1);
    expectedSet.add(expected2);

    CollectionSource source = new CollectionSource(asList(null, p1, null, p3, null));

    CollectionDestination destination = mapper.map(source);

    assertEquals(expectedSet, destination.getCities());
  }

  @Test
  public void shouldDistinctBetweenOtherReplaceOperations_andSkipWhenNull() {
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReplaceCollection(CollectionSource::getPersons, CollectionDestination::getCities)
          .andSkipWhenNull()
          .ensure();
    }).isInstanceOf(AssertionError.class);
  }

  @Test
  public void shouldDistinctBetweenOtherReplaceOperations_andTest() {
    assertThatThrownBy(() -> {
      AssertMapping.of(mapper)
          .expectReplaceCollection(CollectionSource::getPersons, CollectionDestination::getCities)
          .andTest(p -> "String")
          .ensure();
    }).isInstanceOf(AssertionError.class);
  }

}
