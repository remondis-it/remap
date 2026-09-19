package com.remondis.remap.nullness;

import java.util.Map;

/**
 * The destination bean of the map mapping. The value type differs from the source, so a registered mapper performs the
 * conversion.
 */
public class MappedMapValuesBean {

  private Map<String, AddressDto> addresses = Map.of();

  public MappedMapValuesBean() {
  }

  public Map<String, AddressDto> getAddresses() {
    return addresses;
  }

  public void setAddresses(Map<String, AddressDto> addresses) {
    this.addresses = addresses;
  }

}
