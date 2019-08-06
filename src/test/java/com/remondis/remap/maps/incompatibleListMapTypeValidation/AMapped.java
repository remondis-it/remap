package com.remondis.remap.maps.incompatibleListMapTypeValidation;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class AMapped {

  private List<Set<Map<BMapped, CMapped>>> list;

  public AMapped(List<Set<Map<BMapped, CMapped>>> list) {
    super();
    this.list = list;
  }

  public AMapped() {
    super();
  }

  public List<Set<Map<BMapped, CMapped>>> getList() {
    return list;
  }

  public void setList(List<Set<Map<BMapped, CMapped>>> list) {
    this.list = list;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((list == null) ? 0 : list.hashCode());
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
    if (list == null) {
      if (other.list != null)
        return false;
    } else if (!list.equals(other.list))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "AMapped [list=" + list + "]";
  }

}
