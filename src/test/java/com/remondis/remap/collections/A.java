package com.remondis.remap.collections;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.remondis.remap.basic.B;

public class A {

  private Set<String> strings;
  private List<B> bs;

  private List<Set<B>> nestedLists;

  public A() {
    super();
  }

  public void addStrings(String... strings) {
    this.strings = new HashSet<>(Arrays.asList(strings));
  }

  public void addBs(B... bs) {
    this.bs = Arrays.asList(bs);
  }

  public void addNestedLists(@SuppressWarnings("unchecked") Set<B>... lists) {
    this.nestedLists = Arrays.asList(lists);
  }

  /**
   * @return the nestedLists
   */
  public List<Set<B>> getNestedLists() {
    return nestedLists;
  }

  /**
   * @param nestedLists
   *        the nestedLists to set
   */
  public void setNestedLists(List<Set<B>> nestedLists) {
    this.nestedLists = nestedLists;
  }

  /**
   * @return the strings
   */
  public Set<String> getStrings() {
    return strings;
  }

  /**
   * @param strings
   *        the strings to set
   */
  public void setStrings(Set<String> strings) {
    this.strings = strings;
  }

  /**
   * @return the bs
   */
  public List<B> getBs() {
    return bs;
  }

  /**
   * @param bs
   *        the bs to set
   */
  public void setBs(List<B> bs) {
    this.bs = bs;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return "A [strings=" + strings + ", bs=" + bs + "]";
  }

}
