package com.remondis.remap.regression.cyclicRestructureBug;

public class Node {
  private Node child;
  private String name;

  public Node getChild() {
    return child;
  }

  public void setChild(Node child) {
    this.child = child;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
