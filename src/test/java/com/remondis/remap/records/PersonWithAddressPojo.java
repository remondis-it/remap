package com.remondis.remap.records;

/**
 * POJO with a nested complex type for hierarchical mapping tests.
 */
public class PersonWithAddressPojo {

  private String name;
  private AddressPojo address;

  public PersonWithAddressPojo() {
  }

  public PersonWithAddressPojo(String name, AddressPojo address) {
    this.name = name;
    this.address = address;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AddressPojo getAddress() {
    return address;
  }

  public void setAddress(AddressPojo address) {
    this.address = address;
  }

}
