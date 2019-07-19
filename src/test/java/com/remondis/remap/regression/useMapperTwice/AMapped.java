package com.remondis.remap.regression.useMapperTwice;

import java.util.List;

public class AMapped {

  private List<BMapped> bs;

  public AMapped() {
    super();
  }

  public AMapped(List<BMapped> bs) {
    super();
    this.bs = bs;
  }

  public List<BMapped> getBs() {
    return bs;
  }

  public void setBs(List<BMapped> bs) {
    this.bs = bs;
  }

}
