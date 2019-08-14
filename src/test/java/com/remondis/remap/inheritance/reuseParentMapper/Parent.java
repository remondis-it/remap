package com.remondis.remap.inheritance.reuseParentMapper;

public class Parent {

  private String parent;

  public Parent() {
    super();
  }

  public Parent(String parent) {
    super();
    this.parent = parent;
  }

  public String getParent() {
    return parent;
  }

  public void setParent(String parent) {
    this.parent = parent;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((parent == null) ? 0 : parent.hashCode());
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
    Parent other = (Parent) obj;
    if (parent == null) {
      if (other.parent != null)
        return false;
    } else if (!parent.equals(other.parent))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Parent [parent=" + parent + "]";
  }

}
