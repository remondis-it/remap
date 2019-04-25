package com.remondis.remap.collections;

import java.util.Set;

public class ReassignBean {

  private Set<String> anotherName;

  public ReassignBean() {
    super();
  }

  public ReassignBean(Set<String> anotherName) {
    super();
    this.anotherName = anotherName;
  }

  public Set<String> getAnotherName() {
    return anotherName;
  }

  public void setAnotherName(Set<String> anotherName) {
    this.anotherName = anotherName;
  }

}
