package com.remondis.remap.generics;

public class Bean2<T> {

  private T reference;

  public Bean2() {
  }

  public Bean2(T reference) {
    super();
    this.reference = reference;
  }

  public T getReference() {
    return reference;
  }

  public void setReference(T reference) {
    this.reference = reference;
  }

}
