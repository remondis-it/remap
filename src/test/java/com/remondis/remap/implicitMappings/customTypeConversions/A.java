package com.remondis.remap.implicitMappings.customTypeConversions;

import java.util.List;

public class A {

  private CharSequence forname;
  private List<CharSequence> addresses;

  public A() {
    super();
  }

  public A(CharSequence forname, List<CharSequence> addresses) {
    super();
    this.forname = forname;
    this.addresses = addresses;
  }

  public CharSequence getForname() {
    return forname;
  }

  public void setForname(CharSequence forname) {
    this.forname = forname;
  }

  public List<CharSequence> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<CharSequence> addresses) {
    this.addresses = addresses;
  }

}
