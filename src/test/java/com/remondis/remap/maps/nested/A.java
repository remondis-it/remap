package com.remondis.remap.maps.nested;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class A {

  private Map<List<A1>, Map<A2, A3>> map;

  public A() {
    super();
    this.map = new Hashtable<>();
  }

  public void add(List<A1> key, Map<A2, A3> value) {
    this.map.put(key, value);
  }

  public Map<List<A1>, Map<A2, A3>> getMap() {
    return map;
  }

  public void setMap(Map<List<A1>, Map<A2, A3>> map) {
    this.map = map;
  }

  @Override
  public String toString() {
    return "A [bmap=" + map + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((map == null) ? 0 : map.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    A other = (A) obj;
    if (map == null) {
      if (other.map != null)
        return false;
    } else if (!map.equals(other.map))
      return false;
    return true;
  }

}
