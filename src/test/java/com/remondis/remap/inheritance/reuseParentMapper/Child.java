package com.remondis.remap.inheritance.reuseParentMapper;

public class Child extends Parent {

  private String childString;

  public Child() {
    super();
  }

  public Child(String parent, String childString) {
    super(parent);
    this.childString = childString;
  }

  public String getChildString() {
    return childString;
  }

  public void setChildString(String childString) {
    this.childString = childString;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((childString == null) ? 0 : childString.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    Child other = (Child) obj;
    if (childString == null) {
      if (other.childString != null)
        return false;
    } else if (!childString.equals(other.childString))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Child [childString=" + childString + "]";
  }

}
