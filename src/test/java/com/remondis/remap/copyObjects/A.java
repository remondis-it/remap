package com.remondis.remap.copyObjects;

public class A {

  private B b;

  public A() {
    super();
  }

  public A(B b) {
    super();
    this.b = b;
  }

  public B getB() {
    return b;
  }

  public void setB(B b) {
    this.b = b;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((b == null) ? 0 : b.hashCode());
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
    if (b == null) {
      if (other.b != null)
        return false;
    } else if (!b.equals(other.b))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "A [b=" + b + "]";
  }

}
