package com.remondis.remap;

/**
 * This interface defines a transformation from one object to another.
 *
 * @param <S> the source type
 * @param <D> the destination type
 * @author schuettec
 */
@FunctionalInterface
public interface Transform<S, D> {

  /**
   * Transforms the source into a destination object.
   *
   * @param source The source object
   * @return The destination object.
   * @throws MappingException Thrown on any error while performing the transformation.
   */
  D transform(S source) throws MappingException;

}
