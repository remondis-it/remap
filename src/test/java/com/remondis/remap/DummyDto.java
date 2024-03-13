package com.remondis.remap;

import java.util.Objects;

public class DummyDto {

  private String string;

  private String anotherString;

  public DummyDto(String string, String anotherString) {
    super();
    this.string = string;
    this.anotherString = anotherString;
  }

  public DummyDto() {
    super();
  }

  public String getAnotherString() {
    return anotherString;
  }

  public void setAnotherString(String anotherString) {
    this.anotherString = anotherString;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  @Override
  public int hashCode() {
    return Objects.hash(anotherString, string);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    DummyDto other = (DummyDto) obj;
    return Objects.equals(anotherString, other.anotherString) && Objects.equals(string, other.string);
  }

  @Override
  public String toString() {
    return "DummyDto [string=" + string + ", anotherString=" + anotherString + "]";
  }
}
