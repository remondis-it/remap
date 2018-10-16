package com.remondis.remap;

public class AWithOneMoreDestinationField {

  private int zahl;
  private String text;

  public AWithOneMoreDestinationField() {
    super();
  }

  public AWithOneMoreDestinationField(int zahl, String text) {
    super();
    this.zahl = zahl;
    this.text = text;
  }

  public int getZahl() {
    return zahl;
  }

  public void setZahl(int zahl) {
    this.zahl = zahl;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
}
