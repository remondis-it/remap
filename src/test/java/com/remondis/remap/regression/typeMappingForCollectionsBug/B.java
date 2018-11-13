package com.remondis.remap.regression.typeMappingForCollectionsBug;

import java.util.Set;

public class B {
  private Set<String> strings;

  public B() {
    super();
  }

  public B(Set<String> strings) {
    super();
    this.strings = strings;
  }

  public Set<String> getStrings() {
    return strings;
  }

  public void setStrings(Set<String> strings) {
    this.strings = strings;
  }

}
