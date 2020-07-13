package com.remondis.remap;

/**
 * Represents a field value mapping result. This result carries the actual value to write to the destination object and
 * an operation flag indicating situations where a value, <code>null</code> or nothing should be written to destination.
 */
class MappedResult {

  private Object value;

  private MapOp operation;

  private MappedResult(Object value, MapOp operation) {
    super();
    this.value = value;
    this.operation = operation;
  }

  /**
   * @return Returns a {@link MappedResult} that signals that the mapping should be skipped.
   */
  public static MappedResult skip() {
    return new MappedResult(null, MapOp.SKIP);
  }

  /**
   * @param value The actual value the mapping returns.
   * @return Returns a {@link MappedResult} that signals that the mapping should be used even if <code>null</code> is
   *         returned.
   */
  public static MappedResult value(Object value) {
    return new MappedResult(value, MapOp.VALUE);
  }

  public Object getValue() {
    return value;
  }

  public MapOp getOperation() {
    return operation;
  }

  /**
   * @return Returns <code>true</code> if a mapped value is present.
   */
  public boolean hasValue() {
    return MapOp.VALUE == this.getOperation();
  }

  @Override
  public String toString() {
    return "MappedResult [value=" + value + ", operation=" + operation + "]";
  }

}
