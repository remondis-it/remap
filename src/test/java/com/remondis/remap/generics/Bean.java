package com.remondis.remap.generics;

public class Bean<T> {

  private T object;

  public Bean(T object) {
    super();
    this.object = object;
  }

  public Bean() {
  }

  public T getObject() {
    return object;
  }

  public void setObject(T object) {
    this.object = object;
  }

}
