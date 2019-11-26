package com.remondis.remap.deepcopy;

import java.math.BigDecimal;

public class B {

  private BigDecimal bigDecimal;

  public B() {
  }

  public B(BigDecimal bigDecimal) {
    super();
    this.bigDecimal = bigDecimal;
  }

  public BigDecimal getBigDecimal() {
    return bigDecimal;
  }

  public void setBigDecimal(BigDecimal bigDecimal) {
    this.bigDecimal = bigDecimal;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((bigDecimal == null) ? 0 : bigDecimal.hashCode());
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
    if (bigDecimal == null) {
      if (other.bigDecimal != null)
        return false;
    } else if (!bigDecimal.equals(other.bigDecimal))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "B [bigDecimal=" + bigDecimal + "]";
  }

}
