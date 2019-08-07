package com.remondis.remap.collections.fromSetToList;

import java.util.LinkedList;
import java.util.List;

public class AMapped {

  private List<A2> as = new LinkedList<>();

  public AMapped(List<A2> as) {
    super();
    this.as = as;
  }

  public AMapped() {
    super();
  }

  public void add(A2 a) {
    as.add(a);
  }

  public List<A2> getAs() {
    return as;
  }

  public void setAs(List<A2> as) {
    this.as = as;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((as == null) ? 0 : as.hashCode());
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
    if (as == null) {
      if (other.as != null)
        return false;
    } else if (!as.equals(other.as))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "AMapped [as=" + as + "]";
  }

}
