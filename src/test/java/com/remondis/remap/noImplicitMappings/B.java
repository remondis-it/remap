package com.remondis.remap.noImplicitMappings;

public class B {

  private String string;
  private Integer integer;

  public B(String string, Integer integer) {
    super();
    this.string = string;
    this.integer = integer;
  }

  public B() {
    super();
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public Integer getInteger() {
    return integer;
  }

  public void setInteger(Integer integer) {
    this.integer = integer;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((integer == null) ? 0 : integer.hashCode());
    result = prime * result + ((string == null) ? 0 : string.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    B other = (B) obj;
    if (integer == null) {
      if (other.integer != null)
        return false;
    } else if (!integer.equals(other.integer))
      return false;
    if (string == null) {
      if (other.string != null)
        return false;
    } else if (!string.equals(other.string))
      return false;
    return true;
  }

}
