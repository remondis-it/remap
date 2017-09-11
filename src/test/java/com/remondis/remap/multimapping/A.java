package com.remondis.remap.multimapping;

public class A {

  private String aString;
  private B b;

  public A() {
    super();
  }

  public A(String aString, B b) {
    super();
    this.aString = aString;
    this.b = b;
  }

  public String getAString() {
    return aString;
  }

  public void setAString(String aString) {
    this.aString = aString;
  }

  public B getB() {
    return b;
  }

  public void setB(B b) {
    this.b = b;
  }

  @Override
  public String toString() {
    return "A [aString=" + aString + ", b=" + b + "]";
  }

}
