package com.remondis.remap.implicitMappings.sameFieldNames;

import java.util.List;

public class A {

  private B b;
  private List<B> bs;

  public A() {
    super();
  }

  public A(B b, List<B> bs) {
    super();
    this.b = b;
    this.bs = bs;
  }

  public B getB() {
    return b;
  }

  public void setB(B b) {
    this.b = b;
  }

  public List<B> getBs() {
    return bs;
  }

  public void setBs(List<B> bs) {
    this.bs = bs;
  }

}
