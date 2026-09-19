package com.remondis.remap.nullness;

import org.jspecify.annotations.Nullable;

/**
 * A source bean declaring its property as nullable.
 */
public class NullableTextBean {

  private @Nullable String text;

  public NullableTextBean() {
  }

  public NullableTextBean(@Nullable String text) {
    this.text = text;
  }

  public @Nullable String getText() {
    return text;
  }

  public void setText(@Nullable String text) {
    this.text = text;
  }

}
