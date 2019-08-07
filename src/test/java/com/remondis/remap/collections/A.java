package com.remondis.remap.collections;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.remondis.remap.basic.B;

public class A {

  private List<B> bs = new LinkedList<>();

  public A() {
    super();
  }

  public void addBs(B... bs) {
    this.bs = Arrays.asList(bs);
  }

  public List<B> getBs() {
    return bs;
  }

  public void setBs(List<B> bs) {
    this.bs = bs;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((bs == null) ? 0 : bs.hashCode());
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
    A other = (A) obj;
    if (bs == null) {
      if (other.bs != null)
        return false;
    } else if (!bs.equals(other.bs))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "A [bs=" + bs + "]";
  }

}
