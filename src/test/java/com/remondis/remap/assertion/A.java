package com.remondis.remap.assertion;

public class A {

  private String string;
  private B b;

  private String omitted;

  private Integer integer;

  public A() {
    super();
  }

  public A(String string, B b, Integer integer) {
    super();
    this.string = string;
    this.b = b;
    this.integer = integer;
  }

  public String getOmitted() {
    return omitted;
  }

  public void setOmitted(String omitted) {
    this.omitted = omitted;
  }

  public Integer getInteger() {
    return integer;
  }

  public void setInteger(Integer integer) {
    this.integer = integer;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public B getB() {
    return b;
  }

  public void setB(B b) {
    this.b = b;
  }

  @Override
  public String toString() {
    return "A [string=" + string + ", b=" + b + ", integer=" + integer + "]";
  }

}
