package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

public class ReplaceBuilder<S, D, RD, RS> {

  static final String TRANSFORM = "transform";

  private TypedPropertyDescriptor<RS> sourceProperty;
  private TypedPropertyDescriptor<RD> destProperty;
  private Mapping<S, D> mapping;

  ReplaceBuilder(TypedPropertyDescriptor<RS> sourceProperty, TypedPropertyDescriptor<RD> destProperty,
      Mapping<S, D> mapping) {
    super();
    this.sourceProperty = sourceProperty;
    this.destProperty = destProperty;
    this.mapping = mapping;
  }

  /**
   * Transforms the selected fields with applying the specified transform function on the source value. <b>Note: The
   * transform function must check the source value for <code>null</code> itself. Use {@link
   * #withSkipWhenNull(Transform)} to skip on <code>null</code> input values.</b>
   *
   * @param transformation The transform function.
   * @return Returns the {@link Mapping} for further mapping configuration.
   */
  public Mapping<S, D> with(Transform<RD, RS> transformation) {
    denyNull("tranformation", transformation);
    ReplaceTransformation<RD, RS> replace = new ReplaceTransformation<RD, RS>(mapping, sourceProperty.property,
        destProperty.property, transformation, false);
    mapping.addMapping(sourceProperty.property, destProperty.property, replace);
    return mapping;
  }

  /**
   * Transforms the selected fields with applying the specified transform function on the source value. <b>This method
   * skips the execution of the transform function if the source value is null.</b>
   *
   * @param transformation The transform function.
   * @return Returns the {@link Mapping} for further mapping configuration.
   */
  public Mapping<S, D> withSkipWhenNull(Transform<RD, RS> transformation) {
    denyNull("tranformation", transformation);
    ReplaceTransformation<RD, RS> replace = new ReplaceTransformation<RD, RS>(mapping, sourceProperty.property,
        destProperty.property, transformation, true);
    mapping.addMapping(sourceProperty.property, destProperty.property, replace);
    return mapping;
  }
}
