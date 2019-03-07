package com.remondis.remap.implicitMappings.customTypeConversions;

import java.util.List;

public class AResource {

  private String forename;
  private List<String> addresses;

  public AResource() {
    super();
  }

  public AResource(String forename, List<String> addresses) {
    super();
    this.forename = forename;
    this.addresses = addresses;
  }

  public String getForename() {
    return forename;
  }

  public void setForename(String forename) {
    this.forename = forename;
  }

  public List<String> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<String> addresses) {
    this.addresses = addresses;
  }

}