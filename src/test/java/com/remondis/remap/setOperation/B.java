package com.remondis.remap.setOperation;

public class B {

  private String string;
  private Integer integerRef;
  private int integer;

  private String valueSet;

  public B(String string, Integer integerRef, int integer, String valueSet) {
    super();
    this.string = string;
    this.integerRef = integerRef;
    this.integer = integer;
    this.valueSet = valueSet;
  }

  public B() {
    super();
    // TODO Auto-generated constructor stub
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public Integer getIntegerRef() {
    return integerRef;
  }

  public void setIntegerRef(Integer integerRef) {
    this.integerRef = integerRef;
  }

  public int getInteger() {
    return integer;
  }

  public void setInteger(int integer) {
    this.integer = integer;
  }

  public String getValueSet() {
    return valueSet;
  }

  public void setValueSet(String valueSet) {
    this.valueSet = valueSet;
  }

}
