package com.remondis.remap.defaultMethods;

public class DestinationBean implements Interface {

  private String string;

  public DestinationBean(String string) {
    super();
    this.string = string;
  }

  public DestinationBean() {
    super();
  }

  @Override
  public String getString() {
    return string;
  }

  @Override
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
    DestinationBean other = (DestinationBean) obj;
    if (string == null) {
      if (other.string != null)
        return false;
    } else if (!string.equals(other.string))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "SourceBean [string=" + string + "]";
  }

}
