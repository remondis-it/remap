package com.remondis.remap.writeNull;

public class Source {

  private String string;

  public Source() {
    super();
  }

  public Source(String string) {
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
    return "Source [string=" + string + "]";
  }

}
