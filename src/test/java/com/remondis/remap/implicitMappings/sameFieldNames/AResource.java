package com.remondis.remap.implicitMappings.sameFieldNames;

import java.util.List;

public class AResource {

  private BResource b;
  private List<BResource> bs;

  public AResource(BResource b, List<BResource> bs) {
    super();
    this.b = b;
    this.bs = bs;
  }

  public AResource() {
    super();
  }

  public BResource getB() {
    return b;
  }

  public void setB(BResource b) {
    this.b = b;
  }

  public List<BResource> getBs() {
    return bs;
  }

  public void setBs(List<BResource> bs) {
    this.bs = bs;
  }

}
