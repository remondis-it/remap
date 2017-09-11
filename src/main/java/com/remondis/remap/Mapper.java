package com.remondis.remap;

import static com.remondis.remap.ReflectionUtil.getCollector;

import java.util.Collection;

/**
 * This class defines a reusable mapper object to perform multiple mappings for the configured object types.
 *
 * @param <S>
 *          The source type
 * @param <D>
 *          The destination type
 *
 * @author schuettec
 *
 */
public class Mapper<S, D> {

  private Mapping<S, D> mapping;

  Mapper(Mapping<S, D> mapping) {
    super();
    this.mapping = mapping;
  }

  /**
   * @return the mapping
   */
  Mapping<S, D> getMapping() {
    return mapping;
  }

  /**
   * Performs the actual mapping recursively through the object hierarchy.
   *
   * @param source
   *          The source object to map to a new destination object.
   * @return Returns a newly created destination object.
   */
  public D map(S source) {
    return mapping.map(source);
  }

  /**
   * Performs the mapping for the specified {@link Collection}.
   *
   * @param source
   *          The source collection to map to a new collection of destination objects.
   * @return Returns a newly created destination object.
   */
  @SuppressWarnings("unchecked")
  public Collection<D> map(Collection<S> source) {
    return (Collection<D>) source.stream()
                                 .map(this::map)
                                 .collect(getCollector(source));
  }

  /**
   * Performs the mapping for the specified {@link Collection} by performing the map operations in parallel.
   *
   * @param source
   *          The source collection to map to a new collection of destination objects.
   * @return Returns a newly created destination object.
   */
  @SuppressWarnings("unchecked")
  public Collection<D> mapParallel(Collection<S> source) {
    return (Collection<D>) source.parallelStream()
                                 .map(this::map)
                                 .collect(getCollector(source));
  }

  @Override
  public String toString() {
    return mapping.toString();
  }

}
