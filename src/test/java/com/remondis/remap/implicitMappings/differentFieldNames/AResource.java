package com.remondis.remap.implicitMappings.differentFieldNames;

import java.util.List;

public class AResource {

  private BResource bResource;
  private List<BResource> bResources;

  public AResource() {
    super();
  }

  public AResource(BResource bResource, List<BResource> bResources) {
    super();
    this.bResource = bResource;
    this.bResources = bResources;
  }

  public BResource getbResource() {
    return bResource;
  }

  public void setbResource(BResource bResource) {
    this.bResource = bResource;
  }

  public List<BResource> getbResources() {
    return bResources;
  }

  public void setbResources(List<BResource> bResources) {
    this.bResources = bResources;
  }

}
