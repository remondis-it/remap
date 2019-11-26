package com.remondis.remap.deepcopy;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class A {

  private int primitive;
  private Long wrapper;
  private String string;

  private NonJavaBean nonJavaBean;

  private List<B> bs;

  private Set<List<Map<NonJavaBean, B>>> map;

  public A() {
  }

  public A(int primitive, Long wrapper, String string, NonJavaBean nonJavaBean, List<B> bs,
      Set<List<Map<NonJavaBean, B>>> map) {
    super();
    this.primitive = primitive;
    this.wrapper = wrapper;
    this.string = string;
    this.nonJavaBean = nonJavaBean;
    this.bs = bs;
    this.map = map;
  }

  public Set<List<Map<NonJavaBean, B>>> getMap() {
    return map;
  }

  public void setMap(Set<List<Map<NonJavaBean, B>>> map) {
    this.map = map;
  }

  public int getPrimitive() {
    return primitive;
  }

  public void setPrimitive(int primitive) {
    this.primitive = primitive;
  }

  public Long getWrapper() {
    return wrapper;
  }

  public void setWrapper(Long wrapper) {
    this.wrapper = wrapper;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }

  public List<B> getBs() {
    return bs;
  }

  public void setBs(List<B> bs) {
    this.bs = bs;
  }

  public NonJavaBean getNonJavaBean() {
    return nonJavaBean;
  }

  public void setNonJavaBean(NonJavaBean nonJavaBean) {
    this.nonJavaBean = nonJavaBean;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((bs == null) ? 0 : bs.hashCode());
    result = prime * result + ((map == null) ? 0 : map.hashCode());
    result = prime * result + ((nonJavaBean == null) ? 0 : nonJavaBean.hashCode());
    result = prime * result + primitive;
    result = prime * result + ((string == null) ? 0 : string.hashCode());
    result = prime * result + ((wrapper == null) ? 0 : wrapper.hashCode());
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
    if (map == null) {
      if (other.map != null)
        return false;
    } else if (!map.equals(other.map))
      return false;
    if (nonJavaBean == null) {
      if (other.nonJavaBean != null)
        return false;
    } else if (!nonJavaBean.equals(other.nonJavaBean))
      return false;
    if (primitive != other.primitive)
      return false;
    if (string == null) {
      if (other.string != null)
        return false;
    } else if (!string.equals(other.string))
      return false;
    if (wrapper == null) {
      if (other.wrapper != null)
        return false;
    } else if (!wrapper.equals(other.wrapper))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "A [primitive=" + primitive + ", wrapper=" + wrapper + ", string=" + string + ", nonJavaBean=" + nonJavaBean
        + ", bs=" + bs + ", map=" + map + "]";
  }

}
