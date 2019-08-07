package com.remondis.remap.collections.fromSetToList;

import java.util.HashSet;
import java.util.Set;

public class A {

  private Set<A1> as = new HashSet<>();

  public A(Set<A1> as) {
    super();
    this.as = as;
  }

  public A() {
    super();
  }

  public void add(A1 a) {
    as.add(a);
  }

  public Set<A1> getAs() {
    return as;
  }

  public void setAs(Set<A1> as) {
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
    A other = (A) obj;
    if (as == null) {
      if (other.as != null)
        return false;
    } else if (!as.equals(other.as))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "A [as=" + as + "]";
  }

}
