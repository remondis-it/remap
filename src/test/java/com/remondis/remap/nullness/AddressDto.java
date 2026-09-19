package com.remondis.remap.nullness;

/**
 * The destination type of the {@link Address} mapping.
 */
public class AddressDto {

  private String city = "";

  public AddressDto() {
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

}
