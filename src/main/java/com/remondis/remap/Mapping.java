package com.remondis.remap;

/**
 * This is the ReMap API entrypoint to create instances of {@link Mapper}.
 *
 * @author schuettec
 *
 */
public class Mapping {

  /**
   * Specifies the source data type to map from.
   *
   * @param source
   *        the data source type.
   * @return Returns a {@link Types} object for further mapping configuration.
   */
  public static <S> Types<S> from(Class<S> source) {
    return new Types<>(source);
  }

}
