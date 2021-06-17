package com.remondis.remap.writeNull;

public class Destination {

  private String string;

  public Destination() {
    super();
  }

  public Destination(String string) {
    super();
    this.string = string;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  @Override
  public String toString() {
    return "Destination [string=" + string + "]";
  }

}
