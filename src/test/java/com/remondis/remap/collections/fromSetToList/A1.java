package com.remondis.remap.collections.fromSetToList;

public class A1 {

  private String string;

  public A1(String string) {
    super();
    this.string = string;
  }

  public A1() {
    super();
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
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
    A1 other = (A1) obj;
    if (string == null) {
      if (other.string != null)
        return false;
    } else if (!string.equals(other.string))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "A1 [string=" + string + "]";
  }

}
