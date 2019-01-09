package com.remondis.remap.interfaces;

public class DestImpl implements Destination {

  private String string;

  @Override
  public String getStringField() {
    return string;
  }

  @Override
  public void setStringField(String string) {
    this.string = string;
  }

}
