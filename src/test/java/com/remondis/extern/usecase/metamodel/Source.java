package com.remondis.extern.usecase.metamodel;

public class Source {

  private String string;
  private NestedSource nested;

  private Object omitInSource;

  public Source() {
    super();
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public NestedSource getNested() {
    return nested;
  }

  public void setNested(NestedSource nested) {
    this.nested = nested;
  }

  public Object getOmitInSource() {
    return omitInSource;
  }

  public void setOmitInSource(Object omitInSource) {
    this.omitInSource = omitInSource;
  }

  @Override
  public String toString() {
    return "Source [string=" + string + ", nested=" + nested + "]";
  }

}
