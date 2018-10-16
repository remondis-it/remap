package com.remondis.remap;

/**
 * This class defines a projection from a type to another type.
 *
 * @param <S> The source type
 * @param <D> The destination type
 * @author schuettec
 */
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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((destination == null) ? 0 : destination.hashCode());
    result = prime * result + ((source == null) ? 0 : source.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    @SuppressWarnings("rawtypes")
    Projection other = (Projection) obj;
    if (destination == null) {
      if (other.destination != null) {
        return false;
      }
    } else if (!destination.equals(other.destination)) {
      return false;
    }
    if (source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!source.equals(other.source)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Projection [source=" + source + ", destination=" + destination + "]";
  }

}
