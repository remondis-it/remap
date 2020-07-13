package com.remondis.extern.usecase.metamodel;

public class Destination {

  private String doesNotExistInSource;

  private String stringRename;
  private int stringLength;

  private String flattened;

  private NestedDestination nested;

  public Destination(String doesNotExistInSource, String stringRename, int stringLength, String flattened,
      NestedDestination nested) {
    super();
    this.doesNotExistInSource = doesNotExistInSource;
    this.stringRename = stringRename;
    this.stringLength = stringLength;
    this.flattened = flattened;
    this.nested = nested;
  }

  public Destination() {
    super();

  }

  public String getDoesNotExistInSource() {
    return doesNotExistInSource;
  }

  public void setDoesNotExistInSource(String doesNotExistInSource) {
    this.doesNotExistInSource = doesNotExistInSource;
  }

  public String getStringRename() {
    return stringRename;
  }

  public void setStringRename(String stringRename) {
    this.stringRename = stringRename;
  }

  public int getStringLength() {
    return stringLength;
  }

  public void setStringLength(int stringLength) {
    this.stringLength = stringLength;
  }

  public String getFlattened() {
    return flattened;
  }

  public void setFlattened(String flattened) {
    this.flattened = flattened;
  }

  public NestedDestination getNested() {
    return nested;
  }

  public void setNested(NestedDestination nested) {
    this.nested = nested;
  }

  @Override
  public String toString() {
    return "Destination [doesNotExistInSource=" + doesNotExistInSource + ", stringRename=" + stringRename
        + ", stringLength=" + stringLength + ", flattened=" + flattened + ", nested=" + nested + "]";
  }

}
