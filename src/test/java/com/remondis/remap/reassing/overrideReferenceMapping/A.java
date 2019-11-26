package com.remondis.remap.reassing.overrideReferenceMapping;

public class A {

  private String string;
  private Long number;

  private B b;

  public A(String string, Long number, B b) {
    super();
    this.string = string;
    this.number = number;
    this.b = b;
  }

  public A() {
  }

  public B getB() {
    return b;
  }

  public void setB(B b) {
    this.b = b;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public Long getNumber() {
    return number;
  }

  public void setNumber(Long number) {
    this.number = number;
  }

}
