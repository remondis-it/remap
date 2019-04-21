package com.remondis.remap.propertypathmapping;

public class Person {
  String forename;

  String name;

  Address address;

  public Person() {
    super();
  }

  public Person(String forename, String name, Address address) {
    super();
    this.forename = forename;
    this.name = name;
    this.address = address;
  }

  public String getForename() {
    return forename;
  }

  public void setForename(String forename) {
    this.forename = forename;
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

}
