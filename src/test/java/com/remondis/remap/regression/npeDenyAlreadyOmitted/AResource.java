package com.remondis.remap.regression.npeDenyAlreadyOmitted;

public class AResource {

  public String multiMapped1;
  public String multiMapped2;
  public String multiMapped3;
  public String omitted;

  public AResource() {
    super();
  }

  public AResource(String multiMapped1, String multiMapped2, String multiMapped3) {
    super();
    this.multiMapped1 = multiMapped1;
    this.multiMapped2 = multiMapped2;
    this.multiMapped3 = multiMapped3;
  }

  public String getOmitted() {
    return omitted;
  }

  public void setOmitted(String omitted) {
    this.omitted = omitted;
  }

  public String getMultiMapped1() {
    return multiMapped1;
  }

  public void setMultiMapped1(String multiMapped1) {
    this.multiMapped1 = multiMapped1;
  }

  public String getMultiMapped2() {
    return multiMapped2;
  }

  public void setMultiMapped2(String multiMapped2) {
    this.multiMapped2 = multiMapped2;
  }

  public String getMultiMapped3() {
    return multiMapped3;
  }

  public void setMultiMapped3(String multiMapped3) {
    this.multiMapped3 = multiMapped3;
  }

}
