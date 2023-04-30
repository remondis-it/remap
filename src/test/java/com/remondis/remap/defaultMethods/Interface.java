package com.remondis.remap.defaultMethods;

public interface Interface {

  String DEFAULT_STRING = "defaultString";

  default String getString() {
    return DEFAULT_STRING;
  }

  default void setString(String string) {

  }
}
