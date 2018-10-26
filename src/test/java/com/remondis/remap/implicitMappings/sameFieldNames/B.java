package com.remondis.remap.implicitMappings.sameFieldNames;

public class B {

  private String string;

  public B(String string) {
    super();
    this.string = string;
  }

  public B() {
    super();
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

}
