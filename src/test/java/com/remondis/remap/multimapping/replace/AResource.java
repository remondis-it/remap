package com.remondis.remap.multimapping.replace;

public class AResource {

  private String aString;
  private String string;
  private int number;
  private Integer integer;

  public AResource() {
    super();
  }

  public AResource(String aString, String string, int number, Integer integer) {
    super();
    this.aString = aString;
    this.string = string;
    this.number = number;
    this.integer = integer;
  }

  public String getAString() {
    return aString;
  }

  public void setAString(String aString) {
    this.aString = aString;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public Integer getInteger() {
    return integer;
  }

  public void setInteger(Integer integer) {
    this.integer = integer;
  }

  @Override
  public String toString() {
    return "AResource [aString=" + aString + ", string=" + string + ", number=" + number + ", integer=" + integer + "]";
  }

}
