package com.remondis.remap.basic;

public class AWithOneMoreSourceField {

  private int onlyInA;
  private int zahl;
  private String text;

  public AWithOneMoreSourceField() {
    super();
  }

  public AWithOneMoreSourceField(int onlyInA, int zahl, String text) {
    super();
    this.onlyInA = onlyInA;
    this.zahl = zahl;
    this.text = text;
  }

  public int getOnlyInA() {
    return onlyInA;
  }

  public void setOnlyInA(int onlyInA) {
    this.onlyInA = onlyInA;
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "AWithOneMoreSourceField [onlyInA=" + onlyInA + ", zahl=" + zahl + ", text=" + text + "]";
  }

}
