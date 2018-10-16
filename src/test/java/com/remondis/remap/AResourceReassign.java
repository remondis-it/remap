package com.remondis.remap;

public class AResourceReassign {

  private int zahl;
  private int firstNumberInAResource;
  private int secondNumberInAResource;

  public AResourceReassign() {
    super();
  }

  public AResourceReassign(int zahl, int firstNumberInAResource, int secondNumberInAResource) {
    super();
    this.zahl = zahl;
    this.firstNumberInAResource = firstNumberInAResource;
    this.secondNumberInAResource = secondNumberInAResource;
  }

  public int getZahl() {
    return zahl;
  }

  public void setZahl(int zahl) {
    this.zahl = zahl;
  }

  public int getFirstNumberInAResource() {
    return firstNumberInAResource;
  }

  public void setFirstNumberInAResource(int firstNumberInAResource) {
    this.firstNumberInAResource = firstNumberInAResource;
  }

  public int getSecondNumberInAResource() {
    return secondNumberInAResource;
  }

  public void setSecondNumberInAResource(int secondNumberInAResource) {
    this.secondNumberInAResource = secondNumberInAResource;
  }

}
