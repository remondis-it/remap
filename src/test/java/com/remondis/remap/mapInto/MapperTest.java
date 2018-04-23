package com.remondis.remap.mapInto;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertEquals;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import java.util.Arrays;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class MapperTest {

  private static final Integer EXPECTED_AGE = 29;
  private static final String EXPECTED_LASTNAME = "Griffin";
  private static final String EXPECTED_FORENAME = "Peter";
  private static final String EXPECTED_STREET = "Brunnenstraße";
  private static final String EXPECTED_CITY = "Lünen";

  @Test
  public void shouldUseDestinationObjectForNestedMappingsAlso() {
    Address address1 = Address.builder()
        .city("city1")
        .houseNumber(1)
        .street("street1")
        .build();
    Address address2 = Address.builder()
        .city("city2")
        .houseNumber(2)
        .street("street2")
        .build();
    List<Address> addresses = Arrays.asList(address1, address2);
    Person person = Person.builder()
        .age(EXPECTED_AGE)
        .forename("forename")
        .lastname("lastname")
        .addresses(addresses)
        .build();

    AddressLite addressLite1 = AddressLite.builder()
        .city(EXPECTED_CITY)
        .street(EXPECTED_STREET)
        .build();
    AddressLite addressLite2 = AddressLite.builder()
        .city(EXPECTED_CITY)
        .street(EXPECTED_STREET)
        .build();
    List<AddressLite> addressesList = Arrays.asList(addressLite1, addressLite2);
    PersonLite personLite = PersonLite.builder()
        .forename(EXPECTED_FORENAME)
        .lastname(EXPECTED_LASTNAME)
        .addresses(addressesList)
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

    assertThat(mappedPerson.getAge()).isEqualTo(EXPECTED_AGE);
    assertThat(mappedPerson.getForename()).isEqualTo(EXPECTED_FORENAME);
    assertThat(mappedPerson.getLastname()).isEqualTo(EXPECTED_LASTNAME);
    assertThat(mappedPerson.getAddresses()).hasSize(2);
    assertThat(mappedPerson.getAddresses().get(0).getCity()).isEqualTo(EXPECTED_CITY);
    assertThat(mappedPerson.getAddresses().get(0).getHouseNumber()).isNull();
    assertThat(mappedPerson.getAddresses().get(0).getStreet()).isEqualTo(EXPECTED_STREET);
    assertThat(mappedPerson.getAddresses().get(1).getCity()).isEqualTo(EXPECTED_CITY);
    assertThat(mappedPerson.getAddresses().get(1).getHouseNumber()).isNull();
    assertThat(mappedPerson.getAddresses().get(1).getStreet()).isEqualTo(EXPECTED_STREET);
  }

}
