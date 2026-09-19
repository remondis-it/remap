package com.remondis.remap.nullness;

import java.util.List;

/**
 * A bean declaring a collection whose elements are non-null.
 */
public class NonNullElementsBean {

  private List<String> texts = List.of();

  public NonNullElementsBean() {
  }

  public List<String> getTexts() {
    return texts;
  }

  public void setTexts(List<String> texts) {
    this.texts = texts;
  }

}
