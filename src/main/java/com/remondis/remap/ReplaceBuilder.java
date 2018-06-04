package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.util.function.Function;

/**
 * Builds a replace operation.
 *
 * @param <S> The source type.
 * @param <D> The destination type.
 * @param <RS> The source field type.
 * @param <RD> The destination field type.
 *
 */
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
   * #withSkipWhenNull(Function)} to skip on <code>null</code> input values.</b>
   *
   * @param transformation The transform function.
   * @return Returns the {@link Mapping} for further mapping configuration.
   */
  public Mapping<S, D> with(Function<RS, RD> transformation) {
    denyNull("tranformation", transformation);
    ReplaceTransformation<RS, RD> replace = new ReplaceTransformation<>(mapping, sourceProperty.property,
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
  public Mapping<S, D> withSkipWhenNull(Function<RS, RD> transformation) {
    denyNull("tranformation", transformation);
    ReplaceTransformation<RS, RD> replace = new ReplaceTransformation<>(mapping, sourceProperty.property,
        destProperty.property, transformation, true);
    mapping.addMapping(sourceProperty.property, destProperty.property, replace);
    return mapping;
  }
}
