package com.remondis.remap.noImplicitMappings;

import java.util.List;
import java.util.Map;

public class A {

  private List<B> bs;
  private String string;
  private Integer integer;
  private Map<String, String> map;

  public A(List<B> bs, String string, Integer integer, Map<String, String> map) {
    super();
    this.bs = bs;
    this.string = string;
    this.integer = integer;
    this.map = map;
  }

  public A() {
    super();
  }

  public List<B> getBs() {
    return bs;
  }

  public void setBs(List<B> bs) {
    this.bs = bs;
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

  public Map<String, String> getMap() {
    return map;
  }

  public void setMap(Map<String, String> map) {
    this.map = map;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((bs == null) ? 0 : bs.hashCode());
    result = prime * result + ((integer == null) ? 0 : integer.hashCode());
    result = prime * result + ((map == null) ? 0 : map.hashCode());
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
    A other = (A) obj;
    if (bs == null) {
      if (other.bs != null)
        return false;
    } else if (!bs.equals(other.bs))
      return false;
    if (integer == null) {
      if (other.integer != null)
        return false;
    } else if (!integer.equals(other.integer))
      return false;
    if (map == null) {
      if (other.map != null)
        return false;
    } else if (!map.equals(other.map))
      return false;
    if (string == null) {
      if (other.string != null)
        return false;
    } else if (!string.equals(other.string))
      return false;
    return true;
  }

}
