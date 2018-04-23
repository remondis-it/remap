package com.remondis.remap;

public class A {

  private String moreInA;
  private String string;
  private int number;
  private Integer integer;
  private Long zahlInA;
  private B b;

  public A() {
    super();
  }

  public A(String moreInA, String string, int number, Integer integer, Long zahlInA, B b) {
    super();
    this.moreInA = moreInA;
    this.string = string;
    this.number = number;
    this.integer = integer;
    this.zahlInA = zahlInA;
    this.b = b;
  }

  /**
   * @return the string
   */
  public String getString() {
    return string;
  }

  /**
   * @param string
   *        the string to set
   */
  public void setString(String string) {
    this.string = string;
  }

  /**
   * @return the number
   */
  public int getNumber() {
    return number;
  }

  /**
   * @param number
   *        the number to set
   */
  public void setNumber(int number) {
    this.number = number;
  }

  /**
   * @return the integer
   */
  public Integer getInteger() {
    return integer;
  }

  /**
   * @param integer
   *        the integer to set
   */
  public void setInteger(Integer integer) {
    this.integer = integer;
  }

  /**
   * @return the b
   */
  public B getB() {
    return b;
  }

  /**
   * @param b
   *        the b to set
   */
  public void setB(B b) {
    this.b = b;
  }

  /**
   * @return the moreInA
   */
  public String getMoreInA() {
    return moreInA;
  }

  /**
   * @param moreInA
   *        the moreInA to set
   */
  public void setMoreInA(String moreInA) {
    this.moreInA = moreInA;
  }

  /**
   * @return the zahlInA
   */
  public Long getZahlInA() {
    return zahlInA;
  }

  /**
   * @param zahlInA
   *        the zahlInA to set
   */
  public void setZahlInA(Long zahlInA) {
    this.zahlInA = zahlInA;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "A [moreInA=" + moreInA + ", string=" + string + ", number=" + number + ", integer=" + integer + ", zahlInA="
        + zahlInA + ", b=" + b + "]";
  }

}
