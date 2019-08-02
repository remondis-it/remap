package com.remondis.remap.maps.incompatibleListMapTypeValidation;

import java.util.List;
import java.util.Set;

public class A {

  private List<Set<Set<B>>> list;

  public A(List<Set<Set<B>>> list) {
    super();
    this.list = list;
  }

  public A() {
    super();
  }

  public List<Set<Set<B>>> getList() {
    return list;
  }

  public void setList(List<Set<Set<B>>> list) {
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
    A other = (A) obj;
    if (list == null) {
      if (other.list != null)
        return false;
    } else if (!list.equals(other.list))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "A [list=" + list + "]";
  }

}
