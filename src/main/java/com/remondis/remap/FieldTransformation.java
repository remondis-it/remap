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
   * @return Returns the {@link MappedResult}
   * @throws MappingException Throws a mapping exception if the transformation function throws an exception.
   */
  public MappedResult performTransformation(Object source) throws MappingException {
    return transformation.performValueTransformation(transformation.getSourceProperty(),
        transformation.getDestinationProperty(), source, null);
  }

  public Class<?> getSourceType() {
    return transformation.getSourceType();
  }

  public Class<?> getDestinationType() {
    return transformation.getDestinationType();
  }

}
