package com.remondis.remap.restructure.ndepth;

import com.remondis.remap.restructure.Address;

public class Person {
  private String forename;
  private String name;

  private Address address;

  public Person() {
  }

  public Person(Address address) {
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getForename() {
    return forename;
  }

  public void setForename(String forename) {
    this.forename = forename;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }
}
