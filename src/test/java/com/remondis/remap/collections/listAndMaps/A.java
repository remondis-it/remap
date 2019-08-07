package com.remondis.remap.collections.listAndMaps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class A {

  private List<Map<String, B>> nestedLists;

  public A() {
    super();
  }

  public void addNestedLists(@SuppressWarnings("unchecked") Map<String, B>... lists) {
    this.nestedLists = Arrays.asList(lists);
  }

  /**
   * @return the nestedLists
   */
  public List<Map<String, B>> getNestedLists() {
    return nestedLists;
  }

  /**
   * @param nestedLists
   *        the nestedLists to set
   */
  public void setNestedLists(List<Map<String, B>> nestedLists) {
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
