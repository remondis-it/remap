package com.remondis.remap.implicitMappings.customTypeConversions;

import java.util.List;

public class A {

  private List<CharSequence> addresses;

  public A() {
    super();
  }

  public A(List<CharSequence> addresses) {
    super();
    this.addresses = addresses;
  }

  public List<CharSequence> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<CharSequence> addresses) {
    this.addresses = addresses;
  }

}
