package com.remondis.remap.nullness;

/**
 * A destination bean declaring a primitive property. Writing null to it fails with an
 * {@link IllegalArgumentException}.
 */
public class PrimitiveNumberBean {

  private int number;

  public PrimitiveNumberBean() {
  }

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

}
