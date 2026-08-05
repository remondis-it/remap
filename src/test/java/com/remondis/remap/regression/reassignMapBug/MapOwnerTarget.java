package com.remondis.remap.regression.reassignMapBug;

import java.util.LinkedHashMap;

public class MapOwnerTarget {
  private LinkedHashMap<String, String> anotherMap;

  public MapOwnerTarget() {
    super();
  }

  public MapOwnerTarget(LinkedHashMap<String, String> anotherMap) {
    super();
    this.anotherMap = anotherMap;
  }

  public LinkedHashMap<String, String> getAnotherMap() {
    return anotherMap;
  }

  public void setAnotherMap(LinkedHashMap<String, String> anotherMap) {
    this.anotherMap = anotherMap;
  }

  @Override
  public String toString() {
    return "MapOwnerTargetOld [anotherMap=" + anotherMap + "]";
  }

}
