package com.remondis.remap.inheritance.reuseParentMapper;

public class ChildMapped extends ParentMapped {

  private int childInt;

  public ChildMapped(int childInt) {
    super();
    this.childInt = childInt;
  }

  public ChildMapped() {
    super();
  }

  public int getChildInt() {
    return childInt;
  }

  public void setChildInt(int childInt) {
    this.childInt = childInt;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + childInt;
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
    ChildMapped other = (ChildMapped) obj;
    if (childInt != other.childInt)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ChildMapped [childInt=" + childInt + "]";
  }

}
