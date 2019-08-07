package com.remondis.remap.collections.nestedCollections;

import java.util.List;
import java.util.Set;

import com.remondis.remap.basic.BResource;

public class AResource {

  private Set<List<BResource>> nestedLists;

  public AResource() {
    super();
  }

  /**
   * @return the nestedLists
   */
  public Set<List<BResource>> getNestedLists() {
    return nestedLists;
  }

  /**
   * @param nestedLists
   *        the nestedLists to set
   */
  public void setNestedLists(Set<List<BResource>> nestedLists) {
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
