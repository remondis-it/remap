package com.remondis.remap.restructure.grandparentMapper;

public class OuterDto {
  private MidDto mid;
  private String outerName;

  public MidDto getMid() {
    return mid;
  }

  public void setMid(MidDto mid) {
    this.mid = mid;
  }

  public String getOuterName() {
    return outerName;
  }

  public void setOuterName(String outerName) {
    this.outerName = outerName;
  }
}
