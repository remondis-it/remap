package com.remondis.remap.regression.reassignMapBug;

import java.util.LinkedHashMap;

public class MapOwner {
  private LinkedHashMap<String, String> map;

  public MapOwner() {
    super();
  }

  public MapOwner(LinkedHashMap<String, String> map) {
    super();
    this.map = map;
  }

  public LinkedHashMap<String, String> getMap() {
    return map;
  }

  public void setMap(LinkedHashMap<String, String> map) {
    this.map = map;
  }

  @Override
  public String toString() {
    return "MapOwner [map=" + map + "]";
  }

}
