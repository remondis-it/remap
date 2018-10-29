package com.remondis.remap;

/**
 * This is an internal abstraction of the essential mapping function that is used internally by {@link Mapping}. This
 * interface is used to generalize the mapper specified by {@link Mapping#useMapper(Mapper)} and the type mapping
 * functions.
 */
interface InternalMapper<S, D> {
  /**
   * Performs the mapping from the source into a specified destination object. In case of a Java Bean mapper the fields
   * in the destination object are overridden if affected by the mapping configuration. In case of a custom type mapper,
   * the mapping function defines the behaviour.
   *
   * @param source The source object.
   * @param destination The destination object to map into.
   * @return Returns the specified destination object.
   */
  public D map(S source, D destination);

  /**
   * Returns the {@link Projection} this mapper defines.
   * 
   * @return Returns the type projection information.
   */
  public Projection<S, D> getProjection();

}
