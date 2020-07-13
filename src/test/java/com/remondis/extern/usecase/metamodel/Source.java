package com.remondis.extern.usecase.metamodel;

public class Source {

  private String string;
  private int zahl;
  private NestedSource nested;

  public Source(String string, int zahl, NestedSource nested) {
    super();
    this.string = string;
    this.zahl = zahl;
    this.nested = nested;
  }

  public Source() {
    super();
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public int getZahl() {
    return zahl;
  }

  public void setZahl(int zahl) {
    this.zahl = zahl;
  }

  public NestedSource getNested() {
    return nested;
  }

  public void setNested(NestedSource nested) {
    this.nested = nested;
  }

  @Override
  public String toString() {
    return "Source [string=" + string + ", zahl=" + zahl + ", nested=" + nested + "]";
  }

}
