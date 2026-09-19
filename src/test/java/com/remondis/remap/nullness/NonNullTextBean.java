package com.remondis.remap.nullness;

/**
 * A bean declaring its property as non-null without providing a default value. Used as destination this bean holds a
 * null value as long as the property was not written by the mapping.
 */
public class NonNullTextBean {

  private String text;

  public NonNullTextBean() {
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

}
