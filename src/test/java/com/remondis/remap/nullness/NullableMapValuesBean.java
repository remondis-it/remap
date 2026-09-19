package com.remondis.remap.nullness;

import java.util.Map;

import org.jspecify.annotations.Nullable;

/**
 * A source bean declaring a map whose values are nullable and require a registered mapper for the conversion.
 */
public class NullableMapValuesBean {

  private Map<String, @Nullable Address> addresses = Map.of();

  public NullableMapValuesBean() {
  }

  public Map<String, @Nullable Address> getAddresses() {
    return addresses;
  }

  public void setAddresses(Map<String, @Nullable Address> addresses) {
    this.addresses = addresses;
  }

}
