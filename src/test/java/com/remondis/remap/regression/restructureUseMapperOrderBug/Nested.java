package com.remondis.remap.regression.restructureUseMapperOrderBug;

public class Nested {
  private InnerDto inner;
  private String name;

  public InnerDto getInner() {
    return inner;
  }

  public void setInner(InnerDto inner) {
    this.inner = inner;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
