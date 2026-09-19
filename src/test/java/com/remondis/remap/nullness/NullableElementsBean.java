package com.remondis.remap.nullness;

import java.util.List;

import org.jspecify.annotations.Nullable;

/**
 * A source bean declaring a collection whose elements are nullable. ReMap denies null elements while mapping
 * collections.
 */
public class NullableElementsBean {

  private List<@Nullable String> texts = List.of();

  public NullableElementsBean() {
  }

  public List<@Nullable String> getTexts() {
    return texts;
  }

  public void setTexts(List<@Nullable String> texts) {
    this.texts = texts;
  }

}
