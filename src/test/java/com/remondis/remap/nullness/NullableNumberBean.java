package com.remondis.remap.nullness;

import org.jspecify.annotations.Nullable;

/**
 * A source bean declaring a nullable wrapper property that is mapped to a primitive destination property.
 */
public class NullableNumberBean {

  private @Nullable Integer number;

  public NullableNumberBean() {
  }

  public @Nullable Integer getNumber() {
    return number;
  }

  public void setNumber(@Nullable Integer number) {
    this.number = number;
  }

}
