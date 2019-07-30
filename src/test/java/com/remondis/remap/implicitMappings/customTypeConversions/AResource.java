package com.remondis.remap.implicitMappings.customTypeConversions;

import java.util.List;

public class AResource {

  private List<String> addresses;

  public AResource() {
    super();
  }

  public AResource(List<String> addresses) {
    super();
    this.addresses = addresses;
  }

  public List<String> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<String> addresses) {
    this.addresses = addresses;
  }

}