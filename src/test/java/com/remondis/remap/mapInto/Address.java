package com.remondis.remap.mapInto;

public class Address {

  private Integer houseNumber;
  private String street;
  private String city;

  public Address(Integer houseNumber, String street, String city) {
    super();
    this.houseNumber = houseNumber;
    this.street = street;
    this.city = city;
  }

  public Address() {
    super();
  }

  public Integer getHouseNumber() {
    return houseNumber;
  }

  public void setHouseNumber(Integer houseNumber) {
    this.houseNumber = houseNumber;
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

}
