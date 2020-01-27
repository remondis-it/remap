package com.remondis.remap.restructure.demo;

public class Person {

  private String forename;
  private String name;

  private Address address;

  public Person() {
  }

  public Person(String forename, String name, Address address) {
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

  @Override
  public String toString() {
    return "Person{" + "forename='" + forename + '\'' + ", name='" + name + '\'' + ", address=" + address + '}';
  }
}
