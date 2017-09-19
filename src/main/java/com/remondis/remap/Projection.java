package com.remondis.remap;

import lombok.EqualsAndHashCode;

/**
 * This class defines a projection from a type to another type.
 *
 * @param <S> The source type
 * @param <D> The destination type
 * @author schuettec
 */
@EqualsAndHashCode
class Projection<S, D> {

  private Class<S> source;
  private Class<D> destination;

  Projection(Class<S> source, Class<D> destination) {
    super();
    this.source = source;
    this.destination = destination;
  }

  Class<S> getSource() {
    return source;
  }

  Class<D> getDestination() {
    return destination;
  }

  @Override
  public String toString() {
    return "Projection [source=" + source + ", destination=" + destination + "]";
  }

}
