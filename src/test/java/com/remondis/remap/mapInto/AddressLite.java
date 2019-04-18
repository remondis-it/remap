package com.remondis.remap.mapInto;

public class AddressLite {

  private String street;
  private String city;

  public AddressLite(String street, String city) {
    super();
    this.street = street;
    this.city = city;
  }

  public AddressLite() {
    super();
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
