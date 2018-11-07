package com.remondis.remap.implicitMappings.customTypeConversions;

import java.util.List;

public class AResource {

  private String forname;
  private List<String> addresses;

  public AResource() {
    super();
  }

  public AResource(String forname, List<String> addresses) {
    super();
    this.forname = forname;
    this.addresses = addresses;
  }

  public String getForname() {
    return forname;
  }

  public void setForname(String forname) {
    this.forname = forname;
  }

  public List<String> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<String> addresses) {
    this.addresses = addresses;
  }

}