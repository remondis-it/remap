package com.remondis.remap.implicitMappings.customTypeConversions;

import java.util.List;

public class A {

  private CharSequence forename;
  private List<CharSequence> addresses;

  public A() {
    super();
  }

  public A(CharSequence forename, List<CharSequence> addresses) {
    super();
    this.forename = forename;
    this.addresses = addresses;
  }

  public CharSequence getForename() {
    return forename;
  }

  public void setForename(CharSequence forename) {
    this.forename = forename;
  }

  public List<CharSequence> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<CharSequence> addresses) {
    this.addresses = addresses;
  }

}
