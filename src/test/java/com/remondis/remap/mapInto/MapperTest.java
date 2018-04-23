package com.remondis.remap.mapInto;

import java.util.Arrays;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MapperTest {

  private static final int EXPECTED_AGE = 29;
  private static final String EXPECTED_LASTNAME = "Griffin";
  private static final String EXPECTED_FORENAME = "Peter";
  private static final String EXPECTED_STREET = "Brunnenstraße";
  private static final String EXPECTED_CITY = "Lünen";
  private static final int EXPECTED_HOUSE_NUMBER = 23;

  @Test
  public void shouldUseDestinationObjectForNestedMappingsAlso() {
    Address address1 = Address.builder()
        .city("city1")
        .houseNumber(EXPECTED_HOUSE_NUMBER)
        .street("street1")
        .build();
    Address address2 = Address.builder()
        .city("city2")
        .houseNumber(EXPECTED_HOUSE_NUMBER)
        .street("street2")
        .build();
    Person person = Person.builder()
        .age(EXPECTED_AGE)
        .forename("forename")
        .lastname("lastname")
        .addresses(Arrays.asList(address1, address2))
        .build();

    AddressLite addressLite1 = AddressLite.builder()
        .city(EXPECTED_CITY)
        .street(EXPECTED_STREET)
        .build();
    AddressLite addressLite2 = AddressLite.builder()
        .city(EXPECTED_CITY)
        .street(EXPECTED_STREET)
        .build();
    PersonLite personLite = PersonLite.builder()
        .forename(EXPECTED_FORENAME)
        .lastname(EXPECTED_LASTNAME)
        .addresses(Arrays.asList(addressLite1, addressLite2))
        .build();

    Mapper<AddressLite, Address> addressMapper = Mapping.from(AddressLite.class)
        .to(Address.class)
        .omitInDestination(Address::getHouseNumber)
        .mapper();

    Mapper<PersonLite, Person> mapper = Mapping.from(PersonLite.class)
        .to(Person.class)
        .useMapper(addressMapper)
        .omitInDestination(Person::getAge)
        .mapper();

    Person mappedPerson = mapper.map(personLite, person);

  }

}
