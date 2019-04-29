package com.remondis.remap.collections;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.remondis.remap.basic.BResource;

public class AResource {

  private Set<String> strings;
  private List<BResource> bs;

  private List<List<BResource>> nestedLists;

  public AResource() {
    super();
  }

  public void addStrings(String... strings) {
    this.strings = new HashSet<>(Arrays.asList(strings));
  }

  public void addBs(BResource... bs) {
    this.bs = Arrays.asList(bs);
  }

  /**
   * @return the nestedLists
   */
  public List<List<BResource>> getNestedLists() {
    return nestedLists;
  }

  /**
   * @param nestedLists
   *        the nestedLists to set
   */
  public void setNestedLists(List<List<BResource>> nestedLists) {
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
  public List<BResource> getBs() {
    return bs;
  }

  /**
   * @param bs
   *        the bs to set
   */
  public void setBs(List<BResource> bs) {
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
