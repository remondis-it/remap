package com.remondis.remap.inheritance.reuseParentMapper;

public class ParentMapped {

  private int length;

  public ParentMapped() {
    super();
  }

  public ParentMapped(int length) {
    super();
    this.length = length;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + length;
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
    ParentMapped other = (ParentMapped) obj;
    if (length != other.length)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "ParentMapped [length=" + length + "]";
  }

}
