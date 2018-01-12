package com.remondis.remap.regression.replaceOnCollectionBug;

import java.util.List;

public class A {

  private List<String> strings;

  public A(List<String> strings) {
    super();
    this.strings = strings;
  }

  public A() {
    super();
  }

  public List<String> getStrings() {
    return strings;
  }

  public void setStrings(List<String> strings) {
    this.strings = strings;
  }

}
