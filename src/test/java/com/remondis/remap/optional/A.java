package com.remondis.remap.optional;

import static java.util.Arrays.asList;

import java.util.List;
import java.util.Optional;

public class A {

  private Optional<List<String>> optListString;

  private Optional<Optional<String>> optOptString;

  public A(String string) {
    super();
    this.optOptString = Optional.of(Optional.of(string));
    this.optListString = Optional.of(asList(string, string, string));
  }

  public A() {
    super();
  }

  public Optional<Optional<String>> getOptOptString() {
    return optOptString;
  }

  public void setOptOptString(Optional<Optional<String>> optOptString) {
    this.optOptString = optOptString;
  }

  public Optional<List<String>> getOptListString() {
    return optListString;
  }

  public void setOptListString(Optional<List<String>> optListString) {
    this.optListString = optListString;
  }

}
