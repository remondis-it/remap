package com.remondis.remap.mapInto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MapIntoCollectionKeyMatchingTest {

  @Test
  public void shouldMatchCollectionElementsByKeyAndPreserveDestinationFields() {
    // Source: AddressLite without houseNumber
    AddressLite addressLite1 = new AddressLite("Main Street", "Berlin");
    AddressLite addressLite2 = new AddressLite("Oak Avenue", "Munich");
    PersonLite personLite = new PersonLite("Peter", "Griffin", Arrays.asList(addressLite1, addressLite2));

    // Destination: Address with houseNumber set
    Address address1 = new Address(42, "Main Street", "OldCity1");
    Address address2 = new Address(99, "Oak Avenue", "OldCity2");
    Person person = new Person(29, "OldForename", "OldLastname", Arrays.asList(address1, address2));

    Mapper<AddressLite, Address> addressMapper = Mapping.from(AddressLite.class)
        .to(Address.class)
        .omitInDestination(Address::getHouseNumber)
        .mapper();

    Mapper<PersonLite, Person> mapper = Mapping.from(PersonLite.class)
        .to(Person.class)
        .useMapper(addressMapper, AddressLite::getStreet, Address::getStreet)
        .omitInDestination(Person::getAge)
        .mapper();

    Person result = mapper.map(personLite, person);

    // Person-level fields: age preserved (omitted), forename/lastname mapped
    assertThat(result.getAge()).isEqualTo(29);
    assertThat(result.getForename()).isEqualTo("Peter");
    assertThat(result.getLastname()).isEqualTo("Griffin");

    // Collection: elements matched by street key, houseNumber preserved
    assertThat(result.getAddresses()).hasSize(2);

    Address resultAddr1 = result.getAddresses()
        .stream()
        .filter(a -> "Main Street".equals(a.getStreet()))
        .findFirst()
        .orElse(null);
    assertThat(resultAddr1).isNotNull();
    assertThat(resultAddr1.getCity()).isEqualTo("Berlin");
    assertThat(resultAddr1.getHouseNumber()).isEqualTo(42);

    Address resultAddr2 = result.getAddresses()
        .stream()
        .filter(a -> "Oak Avenue".equals(a.getStreet()))
        .findFirst()
        .orElse(null);
    assertThat(resultAddr2).isNotNull();
    assertThat(resultAddr2.getCity()).isEqualTo("Munich");
    assertThat(resultAddr2.getHouseNumber()).isEqualTo(99);
  }

  @Test
  public void shouldCreateNewDestinationElementsForUnmatchedSourceElements() {
    // Source has an element with no matching key in destination
    AddressLite addressLite1 = new AddressLite("Main Street", "Berlin");
    AddressLite addressLite2 = new AddressLite("New Road", "Hamburg");
    PersonLite personLite = new PersonLite("Peter", "Griffin", Arrays.asList(addressLite1, addressLite2));

    // Destination only has "Main Street"
    Address address1 = new Address(42, "Main Street", "OldCity");
    Person person = new Person(29, "OldForename", "OldLastname", Arrays.asList(address1));

    Mapper<AddressLite, Address> addressMapper = Mapping.from(AddressLite.class)
        .to(Address.class)
        .omitInDestination(Address::getHouseNumber)
        .mapper();

    Mapper<PersonLite, Person> mapper = Mapping.from(PersonLite.class)
        .to(Person.class)
        .useMapper(addressMapper, AddressLite::getStreet, Address::getStreet)
        .omitInDestination(Person::getAge)
        .mapper();

    Person result = mapper.map(personLite, person);

    assertThat(result.getAddresses()).hasSize(2);

    // Matched element: houseNumber preserved
    Address matched = result.getAddresses()
        .stream()
        .filter(a -> "Main Street".equals(a.getStreet()))
        .findFirst()
        .orElse(null);
    assertThat(matched).isNotNull();
    assertThat(matched.getHouseNumber()).isEqualTo(42);
    assertThat(matched.getCity()).isEqualTo("Berlin");

    // Unmatched source element: new destination created, houseNumber is null
    Address unmatched = result.getAddresses()
        .stream()
        .filter(a -> "New Road".equals(a.getStreet()))
        .findFirst()
        .orElse(null);
    assertThat(unmatched).isNotNull();
    assertThat(unmatched.getHouseNumber()).isNull();
    assertThat(unmatched.getCity()).isEqualTo("Hamburg");
  }

  @Test
  public void shouldDiscardUnmatchedDestinationElements() {
    // Source has only one element
    AddressLite addressLite1 = new AddressLite("Main Street", "Berlin");
    PersonLite personLite = new PersonLite("Peter", "Griffin", Arrays.asList(addressLite1));

    // Destination has two elements
    Address address1 = new Address(42, "Main Street", "OldCity1");
    Address address2 = new Address(99, "Oak Avenue", "OldCity2");
    Person person = new Person(29, "OldForename", "OldLastname", Arrays.asList(address1, address2));

    Mapper<AddressLite, Address> addressMapper = Mapping.from(AddressLite.class)
        .to(Address.class)
        .omitInDestination(Address::getHouseNumber)
        .mapper();

    Mapper<PersonLite, Person> mapper = Mapping.from(PersonLite.class)
        .to(Person.class)
        .useMapper(addressMapper, AddressLite::getStreet, Address::getStreet)
        .omitInDestination(Person::getAge)
        .mapper();

    Person result = mapper.map(personLite, person);

    // Result should only have one element (from source), destination-only element discarded
    assertThat(result.getAddresses()).hasSize(1);
    assertThat(result.getAddresses()
        .get(0)
        .getStreet()).isEqualTo("Main Street");
    assertThat(result.getAddresses()
        .get(0)
        .getCity()).isEqualTo("Berlin");
    assertThat(result.getAddresses()
        .get(0)
        .getHouseNumber()).isEqualTo(42);
  }

  @Test
  public void shouldFallBackToReplacementWithoutKeyExtractors() {
    // Without key extractors, mapInto should replace the collection entirely (old behavior)
    AddressLite addressLite1 = new AddressLite("Main Street", "Berlin");
    PersonLite personLite = new PersonLite("Peter", "Griffin", Arrays.asList(addressLite1));

    Address address1 = new Address(42, "Main Street", "OldCity");
    Person person = new Person(29, "OldForename", "OldLastname", Arrays.asList(address1));

    Mapper<AddressLite, Address> addressMapper = Mapping.from(AddressLite.class)
        .to(Address.class)
        .omitInDestination(Address::getHouseNumber)
        .mapper();

    // useMapper WITHOUT key extractors
    Mapper<PersonLite, Person> mapper = Mapping.from(PersonLite.class)
        .to(Person.class)
        .useMapper(addressMapper)
        .omitInDestination(Person::getAge)
        .mapper();

    Person result = mapper.map(personLite, person);

    assertThat(result.getAddresses()).hasSize(1);
    assertThat(result.getAddresses()
        .get(0)
        .getStreet()).isEqualTo("Main Street");
    assertThat(result.getAddresses()
        .get(0)
        .getCity()).isEqualTo("Berlin");
    // Without key matching, houseNumber is NOT preserved (new object created)
    assertThat(result.getAddresses()
        .get(0)
        .getHouseNumber()).isNull();
  }

  @Test
  public void shouldWorkWithEmptySourceCollection() {
    PersonLite personLite = new PersonLite("Peter", "Griffin", Arrays.asList());

    Address address1 = new Address(42, "Main Street", "OldCity");
    Person person = new Person(29, "OldForename", "OldLastname", Arrays.asList(address1));

    Mapper<AddressLite, Address> addressMapper = Mapping.from(AddressLite.class)
        .to(Address.class)
        .omitInDestination(Address::getHouseNumber)
        .mapper();

    Mapper<PersonLite, Person> mapper = Mapping.from(PersonLite.class)
        .to(Person.class)
        .useMapper(addressMapper, AddressLite::getStreet, Address::getStreet)
        .omitInDestination(Person::getAge)
        .mapper();

    Person result = mapper.map(personLite, person);

    assertThat(result.getAddresses()).isEmpty();
  }

  @Test
  public void shouldWorkWithEmptyDestinationCollection() {
    AddressLite addressLite1 = new AddressLite("Main Street", "Berlin");
    PersonLite personLite = new PersonLite("Peter", "Griffin", Arrays.asList(addressLite1));

    Person person = new Person(29, "OldForename", "OldLastname", Arrays.asList());

    Mapper<AddressLite, Address> addressMapper = Mapping.from(AddressLite.class)
        .to(Address.class)
        .omitInDestination(Address::getHouseNumber)
        .mapper();

    Mapper<PersonLite, Person> mapper = Mapping.from(PersonLite.class)
        .to(Person.class)
        .useMapper(addressMapper, AddressLite::getStreet, Address::getStreet)
        .omitInDestination(Person::getAge)
        .mapper();

    Person result = mapper.map(personLite, person);

    assertThat(result.getAddresses()).hasSize(1);
    assertThat(result.getAddresses()
        .get(0)
        .getStreet()).isEqualTo("Main Street");
    assertThat(result.getAddresses()
        .get(0)
        .getCity()).isEqualTo("Berlin");
    // No match found, so new object - houseNumber is null
    assertThat(result.getAddresses()
        .get(0)
        .getHouseNumber()).isNull();
  }

  @Test
  public void shouldWorkWithRegularMapWhenKeyExtractorsAreConfigured() {
    // Regular map (not mapInto) should still work as before
    AddressLite addressLite1 = new AddressLite("Main Street", "Berlin");
    PersonLite personLite = new PersonLite("Peter", "Griffin", Arrays.asList(addressLite1));

    Mapper<AddressLite, Address> addressMapper = Mapping.from(AddressLite.class)
        .to(Address.class)
        .omitInDestination(Address::getHouseNumber)
        .mapper();

    Mapper<PersonLite, Person> mapper = Mapping.from(PersonLite.class)
        .to(Person.class)
        .useMapper(addressMapper, AddressLite::getStreet, Address::getStreet)
        .omitInDestination(Person::getAge)
        .mapper();

    // Regular map, not mapInto
    Person result = mapper.map(personLite);

    assertThat(result.getForename()).isEqualTo("Peter");
    assertThat(result.getLastname()).isEqualTo("Griffin");
    assertThat(result.getAge()).isNull();
    assertThat(result.getAddresses()).hasSize(1);
    assertThat(result.getAddresses()
        .get(0)
        .getStreet()).isEqualTo("Main Street");
    assertThat(result.getAddresses()
        .get(0)
        .getCity()).isEqualTo("Berlin");
    assertThat(result.getAddresses()
        .get(0)
        .getHouseNumber()).isNull();
  }

  @Test
  public void shouldHandleDuplicateKeysInSourceByUsingFirstDestinationMatch() {
    // Source has two elements with the same key
    AddressLite addressLite1 = new AddressLite("Main Street", "Berlin");
    AddressLite addressLite2 = new AddressLite("Main Street", "Munich");
    PersonLite personLite = new PersonLite("Peter", "Griffin", Arrays.asList(addressLite1, addressLite2));

    Address address1 = new Address(42, "Main Street", "OldCity");
    Person person = new Person(29, "OldForename", "OldLastname", Arrays.asList(address1));

    Mapper<AddressLite, Address> addressMapper = Mapping.from(AddressLite.class)
        .to(Address.class)
        .omitInDestination(Address::getHouseNumber)
        .mapper();

    Mapper<PersonLite, Person> mapper = Mapping.from(PersonLite.class)
        .to(Person.class)
        .useMapper(addressMapper, AddressLite::getStreet, Address::getStreet)
        .omitInDestination(Person::getAge)
        .mapper();

    Person result = mapper.map(personLite, person);

    // Both source elements match the same destination element by key
    assertThat(result.getAddresses()).hasSize(2);
    // Both should have houseNumber 42 (matched to same destination element)
    List<Address> addresses = result.getAddresses();
    assertThat(addresses.get(0)
        .getHouseNumber()).isEqualTo(42);
    assertThat(addresses.get(1)
        .getHouseNumber()).isEqualTo(42);
    // Cities come from the source elements
    // Both map into the same destination object, so the last mapping wins for shared fields.
    assertThat(addresses.get(0)
        .getCity()).isEqualTo("Munich");
    assertThat(addresses.get(1)
        .getCity()).isEqualTo("Munich");
  }

  @Test
  public void shouldMatchRecordElementsByKeyAndPreserveDestinationComponents() {
    // Source: AddressLite without houseNumber
    PersonLite personLite = new PersonLite("Peter", "Griffin",
        Arrays.asList(new AddressLite("Main Street", "Berlin"), new AddressLite("New Road", "Hamburg")));

    // Destination: record elements with houseNumber set; "Gone Street" has no source counterpart
    AddressRecord existingMatched = new AddressRecord("Main Street", "OldCity", 42);
    AddressRecord existingOrphan = new AddressRecord("Gone Street", "OldCity", 7);
    PersonWithRecordAddresses person = new PersonWithRecordAddresses(29, "OldForename", "OldLastname",
        Arrays.asList(existingMatched, existingOrphan));

    Mapper<AddressLite, AddressRecord> addressMapper = Mapping.from(AddressLite.class)
        .to(AddressRecord.class)
        .omitInDestination(AddressRecord::houseNumber)
        .mapper();

    Mapper<PersonLite, PersonWithRecordAddresses> mapper = Mapping.from(PersonLite.class)
        .to(PersonWithRecordAddresses.class)
        .useMapper(addressMapper, AddressLite::getStreet, AddressRecord::street)
        .omitInDestination(PersonWithRecordAddresses::getAge)
        .mapper();

    PersonWithRecordAddresses result = mapper.map(personLite, person);

    // Container-level fields: age preserved (omitted), names mapped
    assertThat(result.getAge()).isEqualTo(29);
    assertThat(result.getForename()).isEqualTo("Peter");
    assertThat(result.getLastname()).isEqualTo("Griffin");

    assertThat(result.getAddresses()).hasSize(2);

    // Matched element: records are immutable, so a new instance is created with the
    // omitted houseNumber component preserved from the matched destination element.
    AddressRecord matched = result.getAddresses()
        .stream()
        .filter(a -> "Main Street".equals(a.street()))
        .findFirst()
        .orElse(null);
    assertThat(matched).isNotNull();
    assertThat(matched).isNotSameAs(existingMatched);
    assertThat(matched.city()).isEqualTo("Berlin");
    assertThat(matched.houseNumber()).isEqualTo(42);

    // Unmatched source element: mapped to a new record, houseNumber defaults to null
    AddressRecord created = result.getAddresses()
        .stream()
        .filter(a -> "New Road".equals(a.street()))
        .findFirst()
        .orElse(null);
    assertThat(created).isNotNull();
    assertThat(created.city()).isEqualTo("Hamburg");
    assertThat(created.houseNumber()).isNull();

    // Unmatched destination element is discarded
    assertThat(result.getAddresses()).noneMatch(a -> "Gone Street".equals(a.street()));
  }

}
