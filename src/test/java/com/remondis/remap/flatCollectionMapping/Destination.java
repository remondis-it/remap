package com.remondis.remap.flatCollectionMapping;

import java.util.List;

public class Destination {

  private List<Id> ids;

  public Destination(List<Id> ids) {
    super();
    this.ids = ids;
  }

  public Destination() {
    super();
  }

  public List<Id> getIds() {
    return ids;
  }

  public void setIds(List<Id> ids) {
    this.ids = ids;
  }

}
