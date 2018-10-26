package com.remondis.remap.implicitMappings.differentFieldNames;

public class BResource {

  private String anotherString;

  public BResource(String string) {
    super();
    this.anotherString = string;
  }

  public BResource() {
    super();
  }

  public String getAnotherString() {
    return anotherString;
  }

  public void setAnotherString(String anotherString) {
    this.anotherString = anotherString;
  }

}
