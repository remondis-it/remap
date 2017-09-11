package com.remondis.remap;

public class ReplaceBuilder<S, D, RD, RS> {

  TypedPropertyDescriptor<RS> sourceProperty;
  TypedPropertyDescriptor<RD> destProperty;
  public Mapping<S, D> mapping;

  /**
   * Transforms the selected fields with applying the specified transform function on the source value.
   * <b>Note: The transform function must check the source value for <code>null</code> itself. Use
   * {@link #withSkipWhenNull(Transform)} to skip on <code>null</code> input values.</b>
   *
   * @param transform
   *          The transform function.
   * @return Returns the {@link Mapping} for further mapping configuration.
   */
  public Mapping<S, D> with(Transform<RD, RS> transform) {
    ReplaceTransformation<RD, RS> transformation = new ReplaceTransformation<RD, RS>(mapping, sourceProperty.property,
        destProperty.property, transform, false);
    mapping.addMapping(sourceProperty.property, destProperty.property, transformation);
    return mapping;
  }

  /**
   * Transforms the selected fields with applying the specified transform function on the source value. <b>This method
   * skips the execution of the transform function if the source value is null.</b>
   *
   * @param transform
   *          The transform function.
   * @return Returns the {@link Mapping} for further mapping configuration.
   */
  public Mapping<S, D> withSkipWhenNull(Transform<RD, RS> transform) {
    ReplaceTransformation<RD, RS> transformation = new ReplaceTransformation<RD, RS>(mapping, sourceProperty.property,
        destProperty.property, transform, true);
    mapping.addMapping(sourceProperty.property, destProperty.property, transformation);
    return mapping;
  }
}
