package com.remondis.remap.mapover.data;

public class Child {

  private Long attribute1;
  private String attribute2;

  public Child() {
    super();
  }

  public Child(Long attribute1, String attribute2) {
    super();
    this.attribute1 = attribute1;
    this.attribute2 = attribute2;
  }

  public Long getAttribute1() {
    return attribute1;
  }

  public void setAttribute1(Long attribute1) {
    this.attribute1 = attribute1;
  }

  public String getAttribute2() {
    return attribute2;
  }

  public void setAttribute2(String attribute2) {
    this.attribute2 = attribute2;
  }

  @Override
  public String toString() {
    return "Child [attribute1=" + attribute1 + ", attribute2=" + attribute2 + "]";
  }

}
