package com.remondis.remap.mapover.data;

import java.util.List;

public class Root {

  private Child child;

  private List<Child> children;

  public Root() {
    super();
  }

  public Root(Child child, List<Child> children) {
    super();
    this.child = child;
    this.children = children;
  }

  public Child getChild() {
    return child;
  }

  public void setChild(Child child) {
    this.child = child;
  }

  public List<Child> getChildren() {
    return children;
  }

  public void setChildren(List<Child> children) {
    this.children = children;
  }

  @Override
  public String toString() {
    return "Root [child=" + child + ", children=" + children + "]";
  }

}
