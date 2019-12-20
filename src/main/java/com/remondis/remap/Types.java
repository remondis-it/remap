package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.lang.reflect.Constructor;

/**
 * Builder step implementation for the to-method of {@link MappingConfiguration}.
 *
 * @param <S> The source type.
 */
public final class Types<S> {

  private Class<S> source;

  Types(Class<S> source) {
    denyNull("source", source);
    this.source = source;
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

  /**
   * Specifies the destination type of the specified mapping.
   *
   * @param destination The destination type to map to.
   * @return Returns {@link MappingConfiguration} for further mapping configurations.
   */
  public <D> MappingConfiguration<S, D> to(Class<D> destination) {
    denyNull("destination", destination);
    denyNoDefaultConstructor(destination);
    return new MappingConfiguration<>(source, destination);
  }

}
