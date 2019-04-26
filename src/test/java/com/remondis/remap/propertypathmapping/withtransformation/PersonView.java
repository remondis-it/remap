package com.remondis.remap.propertypathmapping.withtransformation;

/**
 *
 */
public class PersonView {

  private int streetLength;
  private Integer streetLengthWrap;

  public PersonView(int streetLength) {
    super();
    this.streetLength = streetLength;
  }

  public PersonView() {
    super();
  }

  public int getStreetLength() {
    return streetLength;
  }

  public void setStreetLength(int streetLength) {
    this.streetLength = streetLength;
  }

  public Integer getStreetLengthWrap() {
    return streetLengthWrap;
  }

  public void setStreetLengthWrap(Integer streetLengthWrap) {
    this.streetLengthWrap = streetLengthWrap;
  }

}
