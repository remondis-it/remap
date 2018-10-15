package com.remondis.remap.regression.omitCustomGet;

import java.util.List;

public class FooMapped {

  private List<Bar> bars;

  public FooMapped() {
    super();
  }

  public FooMapped(List<Bar> bars) {
    super();
    this.bars = bars;
  }

  public List<Bar> getBars() {
    return bars;
  }

  public void setBars(List<Bar> bars) {
    this.bars = bars;
  }

}
