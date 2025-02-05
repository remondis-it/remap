package com.remondis.remap.interfaceMapping;

public class EntityA implements HasId {
  private String name;

  public EntityA() {
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}