package com.remondis.remap.regression.npeOnUnmappedReadOnlyProperty;

public class A {
  private String string;
  private Integer readOnly;

  public A() {
    super();
  }

  public A(String string, Integer readOnly) {
    super();
    this.string = string;
    this.readOnly = readOnly;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public Integer getReadOnly() {
    return readOnly;
  }

}
