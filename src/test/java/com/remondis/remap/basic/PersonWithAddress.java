package com.remondis.remap.basic;

public class PersonWithAddress {

  private String name;
  private Address address;

  public PersonWithAddress() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public static class Address {

    private String street;

    public Address(String street) {
      this.street = street;
    }

    public Address() {
    }

    public String getStreet() {
      return street;
    }

    public void setStreet(String street) {
      this.street = street;
    }
  }
}
