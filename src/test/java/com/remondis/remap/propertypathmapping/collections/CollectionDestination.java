package com.remondis.remap.propertypathmapping.collections;

import java.util.Set;

public class CollectionDestination {

  private Set<String> cities;

  public CollectionDestination(Set<String> cities) {
    super();
    this.cities = cities;
  }

  public CollectionDestination() {
    super();
  }

  public Set<String> getCities() {
    return cities;
  }

  public void setCities(Set<String> cities) {
    this.cities = cities;
  }

}
