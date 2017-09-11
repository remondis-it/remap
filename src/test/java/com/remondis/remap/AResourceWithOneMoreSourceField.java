package com.remondis.remap;

public class AResourceWithOneMoreSourceField {

  private int zahl;
  private String text;

  public AResourceWithOneMoreSourceField() {
    super();
  }

  public AResourceWithOneMoreSourceField(int zahl, String text) {
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

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "AResourceWithOneMoreSourceField [zahl=" + zahl + ", text=" + text + "]";
  }

}
