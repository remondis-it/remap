package com.remondis.remap.basic;

public class BResource {

  private String string;
  private int number;
  private Integer integer;

  public BResource() {
    super();
  }

  public BResource(String string, int number, Integer integer) {
    super();
    this.string = string;
    this.number = number;
    this.integer = integer;
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

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "BResource [string=" + string + ", number=" + number + ", integer=" + integer + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((integer == null) ? 0 : integer.hashCode());
    result = prime * result + number;
    result = prime * result + ((string == null) ? 0 : string.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    BResource other = (BResource) obj;
    if (integer == null) {
      if (other.integer != null)
        return false;
    } else if (!integer.equals(other.integer))
      return false;
    if (number != other.number)
      return false;
    if (string == null) {
      if (other.string != null)
        return false;
    } else if (!string.equals(other.string))
      return false;
    return true;
  }

}
