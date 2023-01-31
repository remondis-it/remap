package com.remondis.remap;

import static java.util.Objects.requireNonNull;

public class ReMapDefaults {

  private static MappingStrategy mappingStrategy = MappingStrategy.PATCH;

  private ReMapDefaults() {
  }

  /**
   * Returns the default mapping strategy used for newly created mappers.
   *
   * @return Default mapping strategy
   */
  public static MappingStrategy getMappingStrategy() {
    return mappingStrategy;
  }

  /**
   * Sets the default mapping strategy that is used for subsequently created mappers.
   *
   * @param mappingStrategy Default mapping strategy
   */
  public static void setMappingStrategy(MappingStrategy mappingStrategy) {
    requireNonNull(mappingStrategy, "mapping strategy must not be null.");
    ReMapDefaults.mappingStrategy = mappingStrategy;
  }
}
