package com.remondis.remap.regression.typeMappingForCollectionsBug;

import java.util.List;

public class A {
  private List<String> strings;

  public A() {
    super();
  }

  public A(List<String> strings) {
    super();
    this.strings = strings;
  }

  public List<String> getStrings() {
    return strings;
  }

  public void setStrings(List<String> strings) {
    this.strings = strings;
  }

}
