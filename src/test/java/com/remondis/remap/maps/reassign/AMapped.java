package com.remondis.remap.maps.reassign;

import java.util.Hashtable;
import java.util.Map;

public class AMapped {

  private Map<Integer, B> anotherName;

  public AMapped() {
    super();
    this.anotherName = new Hashtable<>();
  }

  public Map<Integer, B> getAnotherName() {
    return anotherName;
  }

  public void setAnotherName(Map<Integer, B> anotherName) {
    this.anotherName = anotherName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((anotherName == null) ? 0 : anotherName.hashCode());
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
    AMapped other = (AMapped) obj;
    if (anotherName == null) {
      if (other.anotherName != null)
        return false;
    } else if (!anotherName.equals(other.anotherName))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "AMapped [anotherName=" + anotherName + "]";
  }

}
