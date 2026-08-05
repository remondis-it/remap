package com.remondis.remap.restructure.grandparentMapper;

public class MidDto {
  private LeafDto leaf;
  private String midName;

  public LeafDto getLeaf() {
    return leaf;
  }

  public void setLeaf(LeafDto leaf) {
    this.leaf = leaf;
  }

  public String getMidName() {
    return midName;
  }

  public void setMidName(String midName) {
    this.midName = midName;
  }
}
