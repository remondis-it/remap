package com.remondis.remap.nullness.unmarked;

/**
 * A bean in a package that is deliberately not null-marked. The nullness of its property is unspecified, so it never
 * produces a nullness violation.
 */
public class UnmarkedTextBean {

  private String text;

  public UnmarkedTextBean() {
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

}
