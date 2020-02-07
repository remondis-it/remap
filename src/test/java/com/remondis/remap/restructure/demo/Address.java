package com.remondis.remap.restructure.demo;

public class Address {

  private String street;
  private String houseNumber;

  public Address() {
  }

  public Address(String street, String houseNumber) {
    this.street = street;
    this.houseNumber = houseNumber;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getHouseNumber() {
    return houseNumber;
  }

  public void setHouseNumber(String houseNumber) {
    this.houseNumber = houseNumber;
  }

  @Override
  public String toString() {
    return "Address{" + "street='" + street + '\'' + ", houseNumber='" + houseNumber + '\'' + '}';
  }
}
