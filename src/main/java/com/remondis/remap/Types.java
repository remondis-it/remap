package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.lang.reflect.Constructor;

public final class Types<S> {

  private Class<S> source;

  Types(Class<S> source) {
    denyNull("source", source);
    this.source = source;
    denyNoDefaultConstructor(source);
  }

  private void denyNoDefaultConstructor(Class<?> type) {
    try {
      Constructor<?> constructor = type.getConstructor();
      if (constructor == null) {
        throw MappingException.noDefaultConstructor(type);
      }
    } catch (Exception e) {
      throw MappingException.noDefaultConstructor(type, e);
    }

  }

  public <D> Mapping<S, D> to(Class<D> destination) {
    denyNull("destination", destination);
    denyNoDefaultConstructor(destination);
    return new Mapping<>(source, destination);
  }

}
