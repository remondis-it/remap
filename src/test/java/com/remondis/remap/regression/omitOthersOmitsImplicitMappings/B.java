package com.remondis.remap.regression.omitOthersOmitsImplicitMappings;

public class B {

  private String string1;
  private int string2Length;
  private String someOtherString;

  public B(String string1, int string2Length, String someOtherString) {
    super();
    this.string1 = string1;
    this.string2Length = string2Length;
    this.someOtherString = someOtherString;
  }

  public B() {
    super();
  }

  public String getString1() {
    return string1;
  }

  public void setString1(String string1) {
    this.string1 = string1;
  }

  public int getString2Length() {
    return string2Length;
  }

  public void setString2Length(int string2Length) {
    this.string2Length = string2Length;
  }

  public String getSomeOtherString() {
    return someOtherString;
  }

  public void setSomeOtherString(String someOtherString) {
    this.someOtherString = someOtherString;
  }

}
