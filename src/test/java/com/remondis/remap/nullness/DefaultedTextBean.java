package com.remondis.remap.nullness;

/**
 * A bean declaring its property as non-null and providing a non-null default value. A skipped mapping does not leave a
 * null value behind in this bean.
 */
public class DefaultedTextBean {

  private String text = "default";

  public DefaultedTextBean() {
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

}
