package com.remondis.remap.maps.incompatibleListMapTypeValidation;

public class BMapped {

  private String string;

  public BMapped(String string) {
    super();
    this.string = string;
  }

  public BMapped() {
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
    BMapped other = (BMapped) obj;
    if (string == null) {
      if (other.string != null)
        return false;
    } else if (!string.equals(other.string))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "BMapped [string=" + string + "]";
  }

}
