package com.remondis.remap.inheritance;

import com.remondis.remap.BResource;

public class ParentResource {
  private Object moreInParentResource;
  private Object shouldNotMap;

  private String string;
  private BResource b;

  public ParentResource() {
    super();
  }

  public ParentResource(Object shouldNotMap, String string, BResource b) {
    super();
    this.shouldNotMap = shouldNotMap;
    this.string = string;
    this.b = b;
  }

  /**
   * @return the moreInParentResource
   */
  public Object getMoreInParentResource() {
    return moreInParentResource;
  }

  /**
   * @param moreInParentResource
   *          the moreInParentResource to set
   */
  public void setMoreInParentResource(Object moreInParentResource) {
    this.moreInParentResource = moreInParentResource;
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
  public BResource getB() {
    return b;
  }

  /**
   * @param b
   *          the b to set
   */
  public void setB(BResource b) {
    this.b = b;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "ParentResource [shouldNotMap=" + shouldNotMap + ", string=" + string + ", field=" + b + "]";
  }

}
