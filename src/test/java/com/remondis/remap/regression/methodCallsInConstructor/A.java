package com.remondis.remap.regression.methodCallsInConstructor;

public class A {

  private String aString;

  public A() {
    this.notAValidMethod("toCall");
  }

  public A(String aString) {
    super();
    this.aString = aString;
  }

  public String notAValidMethod(String toCall) {
    return null;
  }

  public String getaString() {
    return aString;
  }

  public void setaString(String aString) {
    this.aString = aString;
  }

}
