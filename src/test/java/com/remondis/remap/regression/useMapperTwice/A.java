package com.remondis.remap.regression.useMapperTwice;

import java.util.List;

public class A {

  private List<B> bs;

  public A() {
    super();
  }

  public A(List<B> bs) {
    super();
    this.bs = bs;
  }

  public List<B> getBs() {
    return bs;
  }

  public void setBs(List<B> bs) {
    this.bs = bs;
  }

}
