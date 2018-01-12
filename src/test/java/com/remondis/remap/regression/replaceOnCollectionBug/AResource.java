package com.remondis.remap.regression.replaceOnCollectionBug;

import java.util.List;

public class AResource {
  private List<String> strings;

  public AResource(List<String> strings) {
    super();
    this.strings = strings;
  }

  public AResource() {
    super();
  }

  public List<String> getStrings() {
    return strings;
  }

  public void setStrings(List<String> strings) {
    this.strings = strings;
  }

}
