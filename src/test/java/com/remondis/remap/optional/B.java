package com.remondis.remap.optional;

import java.util.List;
import java.util.Optional;

public class B {

  private Optional<Optional<String>> optOptString;
  private Optional<String> optString;
  private String string;

  private Optional<Optional<Integer>> optOptInteger;
  private Optional<Integer> optInteger;
  private Integer integer;

  private List<Integer> integers;

  public B() {
    super();
  }

  public B(Optional<Optional<String>> optOptString, Optional<String> optString, String string,
      Optional<Optional<Integer>> optOptInteger, Optional<Integer> optInteger, Integer integer) {
    super();
    this.optOptString = optOptString;
    this.optString = optString;
    this.string = string;
    this.optOptInteger = optOptInteger;
    this.optInteger = optInteger;
    this.integer = integer;
  }

  public List<Integer> getIntegers() {
    return integers;
  }

  public void setIntegers(List<Integer> integers) {
    this.integers = integers;
  }

  public Optional<Optional<String>> getOptOptString() {
    return optOptString;
  }

  public void setOptOptString(Optional<Optional<String>> optOptString) {
    this.optOptString = optOptString;
  }

  public Optional<String> getOptString() {
    return optString;
  }

  public void setOptString(Optional<String> optString) {
    this.optString = optString;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public Optional<Optional<Integer>> getOptOptInteger() {
    return optOptInteger;
  }

  public void setOptOptInteger(Optional<Optional<Integer>> optOptInteger) {
    this.optOptInteger = optOptInteger;
  }

  public Optional<Integer> getOptInteger() {
    return optInteger;
  }

  public void setOptInteger(Optional<Integer> optInteger) {
    this.optInteger = optInteger;
  }

  public Integer getInteger() {
    return integer;
  }

  public void setInteger(Integer integer) {
    this.integer = integer;
  }

  @Override
  public String toString() {
    return "B [optOptString=" + optOptString + ", optString=" + optString + ", string=" + string + ", optOptInteger="
        + optOptInteger + ", optInteger=" + optInteger + ", integer=" + integer + "]";
  }

}
