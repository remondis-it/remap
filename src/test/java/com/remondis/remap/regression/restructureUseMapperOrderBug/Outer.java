package com.remondis.remap.regression.restructureUseMapperOrderBug;

public class Outer {
  private Inner inner;
  private String name;

  public Inner getInner() {
    return inner;
  }

  public void setInner(Inner inner) {
    this.inner = inner;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
