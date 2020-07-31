package com.remondis.extern.usecase.metamodel;

public class NestedSource {

  private String string;

  public NestedSource(String string) {
    super();
    this.string = string;
  }

  public NestedSource() {
    super();
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
    return "NestedSource [string=" + string + "]";
  }

}
