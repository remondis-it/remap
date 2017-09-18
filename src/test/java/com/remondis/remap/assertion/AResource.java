package com.remondis.remap.assertion;

public class AResource {

  private String anotherString;
  private BResource b;

  private Integer omitted;

  private String integerAsString;

  public AResource() {
    super();
  }

  public AResource(String anotherString, BResource b, String integerAsString) {
    super();
    this.anotherString = anotherString;
    this.b = b;
    this.integerAsString = integerAsString;
  }

  public Integer getOmitted() {
    return omitted;
  }

  public void setOmitted(Integer omitted) {
    this.omitted = omitted;
  }

  public String getIntegerAsString() {
    return integerAsString;
  }

  public void setIntegerAsString(String integerAsString) {
    this.integerAsString = integerAsString;
  }

  public String getAnotherString() {
    return anotherString;
  }

  public void setAnotherString(String anotherString) {
    this.anotherString = anotherString;
  }

  public BResource getB() {
    return b;
  }

  public void setB(BResource b) {
    this.b = b;
  }

  @Override
  public String toString() {
    return "AResource [anotherString=" + anotherString + ", b=" + b + ", integerAsString=" + integerAsString + "]";
  }

}
