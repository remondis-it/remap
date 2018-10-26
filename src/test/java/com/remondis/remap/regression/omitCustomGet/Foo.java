package com.remondis.remap.regression.omitCustomGet;

import java.util.List;
import java.util.stream.Collectors;

public class Foo {

  private List<Bar> bars;

  public Foo() {
    super();
  }

  public Foo(List<Bar> bars) {
    super();
    this.bars = bars;
  }

  public List<Bar> getBars() {
    return bars;
  }

  public void setBars(List<Bar> bars) {
    this.bars = bars;
  }

  public List<Bar> getFilteredBars() {
    return this.bars.stream()
        .filter(b -> b.getString()
            .equals("hello"))
        .collect(Collectors.toList());
  }

}
