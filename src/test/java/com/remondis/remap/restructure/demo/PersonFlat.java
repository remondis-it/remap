package com.remondis.remap.restructure.demo;

public class PersonFlat {
  private String forename;
  private String name;

  private String street;
  private String houseNumber;

  public PersonFlat() {
  }

  public PersonFlat(String forename, String name, String street, String houseNumber) {
    this.forename = forename;
    this.name = name;
    this.street = street;
    this.houseNumber = houseNumber;
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
}
