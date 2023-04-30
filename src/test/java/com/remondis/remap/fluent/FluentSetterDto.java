package com.remondis.remap.fluent;

public class FluentSetterDto {

  private Integer integer;
  private int i;
  private String s;
  private boolean b1;
  private boolean b2;

  public Integer getInteger() {
    return integer;
  }

  public FluentSetterDto setInteger(Integer integer) {
    this.integer = integer;
    return this;
  }

  public int getI() {
    return i;
  }

  public FluentSetterDto setI(int i) {
    this.i = i;
    return this;
  }

  public String getS() {
    return s;
  }

  public FluentSetterDto setS(String s) {
    this.s = s;
    return this;
  }

  public boolean getB1() {
    return b1;
  }

  public FluentSetterDto setB1(boolean b) {
    this.b1 = b;
    return this;
  }

  public boolean isB2() {
    return b2;
  }

  public FluentSetterDto setB2(boolean b) {
    this.b2 = b;
    return this;
  }

}
