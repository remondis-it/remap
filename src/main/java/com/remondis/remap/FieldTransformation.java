package com.remondis.remap;

public class FieldTransformation {

  Transformation transformation;

  private FieldTransformation(Transformation transformation) {
    super();
    this.transformation = transformation;
  }

  static FieldTransformation of(Transformation transformation) {
    return new FieldTransformation(transformation);
  }

  /**
   * Performs a single field transformation.
   *
   * @param source The source object.
   * @param destination The destination object.
   * @throws MappingException Throws a mapping exception if the transformation function throws an exception.
   */
  public void performTransformation(Object source, Object destination) throws MappingException {
    transformation.performTransformation(source, destination);
  }

  public Class<?> getSourceType() {
    return transformation.getSourceType();
  }

  public Class<?> getDestinationType() {
    return transformation.getDestinationType();
  }

}
