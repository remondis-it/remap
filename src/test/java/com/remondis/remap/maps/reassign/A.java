package com.remondis.remap.maps.reassign;

import java.util.Hashtable;
import java.util.Map;

public class A {

  private Map<Integer, B> bmap;

  public A() {
    super();
    this.bmap = new Hashtable<>();

  }

  public A addB(Integer key, B b) {
    bmap.put(key, b);
    return this;
  }

  public Map<Integer, B> getBmap() {
    return bmap;
  }

  public void setBmap(Map<Integer, B> bmap) {
    this.bmap = bmap;
  }

  @Override
  public String toString() {
    return "A [bmap=" + bmap + "]";
  }

}
