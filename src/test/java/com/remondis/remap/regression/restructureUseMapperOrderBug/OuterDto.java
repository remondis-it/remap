package com.remondis.remap.regression.restructureUseMapperOrderBug;

public class OuterDto {
  private Nested nested;

  public Nested getNested() {
    return nested;
  }

  public void setNested(Nested nested) {
    this.nested = nested;
  }
}
