package com.remondis.remap;

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

  public D map(S source) {
    return mapping.map(source);
  }

  @Override
  public String toString() {
    return mapping.toString();
  }

}
