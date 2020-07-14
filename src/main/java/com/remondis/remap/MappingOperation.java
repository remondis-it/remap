package com.remondis.remap;

/**
 * Specifies how the mapping result should be used.
 */
public enum MappingOperation {
  /**
   * The mapping does not produce a value. Used to distinguish a <code>null</code>-value from "mapping has no result".
   */
  SKIP,
  /**
   * Signals that the value should be used as mapping result. If value ist <code>null</code>, <code>null</code> should
   * be used as mapping result value.
   */
  VALUE;
}
