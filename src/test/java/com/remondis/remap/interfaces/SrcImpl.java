package com.remondis.remap.interfaces;

public class SrcImpl implements Source {

  private String aString;

  public SrcImpl(String aString) {
    super();
    this.aString = aString;
  }

  @Override
  public String getString() {
    return aString;
  }

  public void setString(String string) {
    this.aString = string;
  }
}
