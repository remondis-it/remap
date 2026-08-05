package com.remondis.remap.records;

/**
 * A nested POJO for testing hierarchical mapping with records.
 */
public class AddressPojo {

  private String street;
  private String city;

  public AddressPojo() {
  }

  public AddressPojo(String street, String city) {
    this.street = street;
    this.city = city;
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
