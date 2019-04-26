package com.remondis.remap.implicitMappings.sameFieldAndType;

import java.util.List;

public class AResource {

  private B b;
  private List<B> bs;

  public AResource(B b, List<B> bs) {
    super();
    this.b = b;
    this.bs = bs;
  }

  public AResource() {
    super();
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
