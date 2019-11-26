package com.remondis.remap.deepcopy;

public class NonJavaBean {

  private String readOnly;

  public NonJavaBean() {
  }

  public NonJavaBean(String readOnly) {
    super();
    this.readOnly = readOnly;
  }

  public String getReadOnly() {
    return readOnly;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((readOnly == null) ? 0 : readOnly.hashCode());
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
    NonJavaBean other = (NonJavaBean) obj;
    if (readOnly == null) {
      if (other.readOnly != null)
        return false;
    } else if (!readOnly.equals(other.readOnly))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "NonJavaBean [readOnly=" + readOnly + "]";
  }

}
