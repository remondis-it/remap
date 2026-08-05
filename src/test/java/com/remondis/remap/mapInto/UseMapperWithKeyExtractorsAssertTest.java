package com.remondis.remap.mapInto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class UseMapperWithKeyExtractorsAssertTest {

  @Test
  public void expectUseMapper_shouldPassWhenMapperAndKeyExtractorsAreRegistered() {
    Mapper<AddressLite, Address> addressMapper = Mapping.from(AddressLite.class)
        .to(Address.class)
        .omitInDestination(Address::getHouseNumber)
        .mapper();

    Mapper<PersonLite, Person> mapper = Mapping.from(PersonLite.class)
        .to(Person.class)
        .useMapper(addressMapper, AddressLite::getStreet, Address::getStreet)
        .omitInDestination(Person::getAge)
        .mapper();

    AssertMapping.of(mapper)
        .expectUseMapper(addressMapper, AddressLite::getStreet, Address::getStreet)
        .expectOmitInDestination(Person::getAge)
        .ensure();
  }

  @Test
  public void expectUseMapper_shouldFailWhenMapperRegisteredWithoutKeyExtractors() {
    Mapper<AddressLite, Address> addressMapper = Mapping.from(AddressLite.class)
        .to(Address.class)
        .omitInDestination(Address::getHouseNumber)
        .mapper();

    // Mapper registered WITHOUT key extractors
    Mapper<PersonLite, Person> mapper = Mapping.from(PersonLite.class)
        .to(Person.class)
        .useMapper(addressMapper)
        .omitInDestination(Person::getAge)
        .mapper();

    assertThatThrownBy(() -> AssertMapping.of(mapper)
        .expectUseMapper(addressMapper, AddressLite::getStreet, Address::getStreet)
        .expectOmitInDestination(Person::getAge)
        .ensure()).isInstanceOf(AssertionError.class)
        .hasMessageContaining("collection key extractors");
  }

  @Test
  public void expectUseMapper_shouldFailWhenMapperNotRegisteredAtAll() {
    Mapper<AddressLite, Address> addressMapper = Mapping.from(AddressLite.class)
        .to(Address.class)
        .omitInDestination(Address::getHouseNumber)
        .mapper();

    // No addressMapper registered: addresses field is omitted on both sides instead
    Mapper<PersonLite, Person> mapper = Mapping.from(PersonLite.class)
        .to(Person.class)
        .omitInSource(PersonLite::getAddresses)
        .omitInDestination(Person::getAge)
        .omitInDestination(Person::getAddresses)
        .mapper();

    assertThatThrownBy(() -> AssertMapping.of(mapper)
        .expectUseMapper(addressMapper, AddressLite::getStreet, Address::getStreet)
        .expectOmitInSource(PersonLite::getAddresses)
        .expectOmitInDestination(Person::getAge)
        .expectOmitInDestination(Person::getAddresses)
        .ensure()).isInstanceOf(AssertionError.class)
        .hasMessageContaining("no mapper was found");
  }

}
