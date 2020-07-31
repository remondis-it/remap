package com.remondis.extern.usecase.metamodel;

public class NestedDestination {

  private String stringRenamed;

  public NestedDestination(String stringRenamed) {
    super();
    this.stringRenamed = stringRenamed;
  }

  public NestedDestination() {
    super();
  }

  public String getStringRenamed() {
    return stringRenamed;
  }

  public void setStringRenamed(String stringRenamed) {
    this.stringRenamed = stringRenamed;
  }

  @Override
  public String toString() {
    return "NestedDestination [stringRenamed=" + stringRenamed + "]";
  }

}
