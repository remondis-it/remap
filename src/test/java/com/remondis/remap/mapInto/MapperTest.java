package com.remondis.remap.mapInto;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MapperTest {

  private static final Integer EXPECTED_AGE = 29;
  private static final String EXPECTED_LASTNAME = "Griffin";
  private static final String EXPECTED_FORENAME = "Peter";
  private static final String EXPECTED_STREET = "Brunnenstraße";
  private static final String EXPECTED_CITY = "Lünen";

  @Test
  public void shouldUseDestinationObjectForNestedMappingsAlso() {
    Address address1 = new Address(1, "street1", "city1");
    Address address2 = new Address(2, "street2", "city2");
    List<Address> addresses = Arrays.asList(address1, address2);
    Person person = new Person(EXPECTED_AGE, "forename", "lastname", addresses);

    AddressLite addressLite1 = new AddressLite(EXPECTED_STREET, EXPECTED_CITY);
    AddressLite addressLite2 = new AddressLite(EXPECTED_STREET, EXPECTED_CITY);
    List<AddressLite> addressesList = Arrays.asList(addressLite1, addressLite2);
    PersonLite personLite = new PersonLite(EXPECTED_FORENAME, EXPECTED_LASTNAME, addressesList);

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
    assertThat(mappedPerson.getAddresses()
        .get(0)
        .getCity()).isEqualTo(EXPECTED_CITY);
    assertThat(mappedPerson.getAddresses()
        .get(0)
        .getHouseNumber()).isNull();
    assertThat(mappedPerson.getAddresses()
        .get(0)
        .getStreet()).isEqualTo(EXPECTED_STREET);
    assertThat(mappedPerson.getAddresses()
        .get(1)
        .getCity()).isEqualTo(EXPECTED_CITY);
    assertThat(mappedPerson.getAddresses()
        .get(1)
        .getHouseNumber()).isNull();
    assertThat(mappedPerson.getAddresses()
        .get(1)
        .getStreet()).isEqualTo(EXPECTED_STREET);
  }

}
