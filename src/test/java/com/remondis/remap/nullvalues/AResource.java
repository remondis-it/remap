package com.remondis.remap.nullvalues;

public class AResource {

  private String moreInAResource;

  private String string;
  private int number;
  private Integer integer;
  private Long zahlInAResource;
  private BResource b;

  private B otherNamedB;

  public AResource() {
    super();
  }

  public AResource(String string, int number, Integer integer, BResource b) {
    super();
    this.string = string;
    this.integer = integer;
    this.b = b;
  }

  public B getOtherNamedB() {
    return otherNamedB;
  }

  public void setOtherNamedB(B otherNamedB) {
    this.otherNamedB = otherNamedB;
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
  public BResource getB() {
    return b;
  }

  /**
   * @param b
   *        the b to set
   */
  public void setB(BResource b) {
    this.b = b;
  }

  /**
   * @return the moreInAResource
   */
  public String getMoreInAResource() {
    return moreInAResource;
  }

  /**
   * @param moreInAResource
   *        the moreInAResource to set
   */
  public void setMoreInAResource(String moreInAResource) {
    this.moreInAResource = moreInAResource;
  }

  /**
   * @return the zahlInAResource
   */
  public Long getZahlInAResource() {
    return zahlInAResource;
  }

  /**
   * @param zahlInAResource
   *        the zahlInAResource to set
   */
  public void setZahlInAResource(Long zahlInAResource) {
    this.zahlInAResource = zahlInAResource;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "AResource [moreInAResource=" + moreInAResource + ", string=" + string + ", number=" + number + ", integer="
            + integer + ", zahlInAResource=" + zahlInAResource + ", b=" + b + "]";
  }

}
