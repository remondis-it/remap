package com.remondis.remap.collections.listAndMaps;

import java.util.Map;
import java.util.Set;

public class AResource {

  private Set<Map<String, BResource>> nestedLists;

  public AResource() {
    super();
  }

  /**
   * @return the nestedLists
   */
  public Set<Map<String, BResource>> getNestedLists() {
    return nestedLists;
  }

  /**
   * @param nestedLists
   *        the nestedLists to set
   */
  public void setNestedLists(Set<Map<String, BResource>> nestedLists) {
    this.nestedLists = nestedLists;
  }

  @Override
  public String toString() {
    return "AResource [nestedLists=" + nestedLists + "]";
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
    AResource other = (AResource) obj;
    if (nestedLists == null) {
      if (other.nestedLists != null)
        return false;
    } else if (!nestedLists.equals(other.nestedLists))
      return false;
    return true;
  }

}
