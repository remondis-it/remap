package com.remondis.remap.inheritance;

import com.remondis.remap.B;

public class Parent {

  private Object moreInParent;
  private Object shouldNotMap;

  private String string;
  private B b;

  public Parent() {
    super();
  }

  public Parent(Object shouldNotMap, String string, B b) {
    super();
    this.shouldNotMap = shouldNotMap;
    this.string = string;
    this.b = b;
  }

  /**
   * @return the moreInParent
   */
  public Object getMoreInParent() {
    return moreInParent;
  }

  /**
   * @param moreInParent
   *          the moreInParent to set
   */
  public void setMoreInParent(Object moreInParent) {
    this.moreInParent = moreInParent;
  }

  /**
   * @return the shouldNotMap
   */
  public Object getShouldNotMap() {
    return shouldNotMap;
  }

  /**
   * @param shouldNotMap
   *          the shouldNotMap to set
   */
  public void setShouldNotMap(Object shouldNotMap) {
    this.shouldNotMap = shouldNotMap;
  }

  /**
   * @return the string
   */
  public String getString() {
    return string;
  }

  /**
   * @param string
   *          the string to set
   */
  public void setString(String string) {
    this.string = string;
  }

  /**
   * @return the b
   */
  public B getB() {
    return b;
  }

  /**
   * @param b
   *          the b to set
   */
  public void setB(B b) {
    this.b = b;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "Parent [shouldNotMap=" + shouldNotMap + ", string=" + string + ", field=" + b + "]";
  }

}
