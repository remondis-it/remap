package com.remondis.remap;

import java.lang.reflect.ParameterizedType;

public abstract class GenericTypeReference<S> {

  private ParameterizedType type;

  public GenericTypeReference() {
    ParameterizedType thisClass = (ParameterizedType) getClass().getGenericSuperclass();
    type = (ParameterizedType) thisClass.getActualTypeArguments()[0];
  }

  @SuppressWarnings("unchecked")
  public Class<S> getGenericClass() {
    return (Class<S>) type.getRawType();
  }
}
