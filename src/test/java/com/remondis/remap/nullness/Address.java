package com.remondis.remap.nullness;

/**
 * A nested bean that requires a registered mapper to be converted into {@link AddressDto}.
 */
public class Address {

  private String city = "";

  public Address() {
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

}
