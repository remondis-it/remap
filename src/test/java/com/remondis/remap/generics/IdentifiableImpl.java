package com.remondis.remap.generics;

public class IdentifiableImpl implements Identifiable<Long> {

  private Long id;

  public IdentifiableImpl() {
    super();
  }

  public IdentifiableImpl(Long id) {
    super();
    this.id = id;
  }

  @Override
  public Long getId() {
    return id;
  }

  @Override
  public void setId(Long id) {
    this.id = id;
  }

}
