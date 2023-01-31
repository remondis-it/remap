package com.remondis.remap;

/**
 * Specifies how the mapping onto a destination object should be performed.
 */
public enum MappingStrategy {
  /**
   * The mapper writes null values if the corresponding source values are null.
   */
  PUT,
  /**
   * The mapper skips attributes of the source object with null values.
   */
  PATCH
}
