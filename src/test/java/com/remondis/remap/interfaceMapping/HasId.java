package com.remondis.remap.interfaceMapping;

public interface HasId {
  default Long getId() {
    return 1L;
  }
}