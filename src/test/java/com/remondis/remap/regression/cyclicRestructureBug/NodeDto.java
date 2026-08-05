package com.remondis.remap.regression.cyclicRestructureBug;

public class NodeDto {
  private NodeDto child;
  private String name;

  public NodeDto getChild() {
    return child;
  }

  public void setChild(NodeDto child) {
    this.child = child;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
