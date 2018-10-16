package com.remondis.remap;

public class AReassign {

  private int zahl;
  private int firstNumberInA;
  private int secondNumberInA;

  public AReassign() {
    super();
  }

  public AReassign(int zahl, int firstNumberInA, int secondNumberInA) {
    super();
    this.zahl = zahl;
    this.firstNumberInA = firstNumberInA;
    this.secondNumberInA = secondNumberInA;
  }

  public int getZahl() {
    return zahl;
  }

  public void setZahl(int zahl) {
    this.zahl = zahl;
  }

  public int getFirstNumberInA() {
    return firstNumberInA;
  }

  public void setFirstNumberInA(int firstNumberInA) {
    this.firstNumberInA = firstNumberInA;
  }

  public int getSecondNumberInA() {
    return secondNumberInA;
  }

  public void setSecondNumberInA(int secondNumberInA) {
    this.secondNumberInA = secondNumberInA;
  }

}
