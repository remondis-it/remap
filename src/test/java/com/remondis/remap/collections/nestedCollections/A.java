package com.remondis.remap.collections.nestedCollections;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.remondis.remap.basic.B;

public class A {

  private List<Set<B>> nestedLists;

  public A() {
    super();
  }

  public void addNestedLists(@SuppressWarnings("unchecked") Set<B>... lists) {
    this.nestedLists = Arrays.asList(lists);
  }

  /**
   * @return the nestedLists
   */
  public List<Set<B>> getNestedLists() {
    return nestedLists;
  }

  /**
   * @param nestedLists
   *        the nestedLists to set
   */
  public void setNestedLists(List<Set<B>> nestedLists) {
    this.nestedLists = nestedLists;
  }

  @Override
  public String toString() {
    return "A [nestedLists=" + nestedLists + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((nestedLists == null) ? 0 : nestedLists.hashCode());
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
    if (nestedLists == null) {
      if (other.nestedLists != null)
        return false;
    } else if (!nestedLists.equals(other.nestedLists))
      return false;
    return true;
  }

}
