package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.util.Collection;

/**
 * Builds a replace collection operation.
 *
 * @param <S> The source type.
 * @param <D> The destination type.
 * @param <RS> The source field type.
 * @param <RD> The destination field type.
 */
public class ReplaceCollectionBuilder<S, D, RD, RS> {

  static final String TRANSFORM = "transform";

  private TypedPropertyDescriptor<Collection<RS>> sourceProperty;
  private TypedPropertyDescriptor<Collection<RD>> destProperty;
  private Mapping<S, D> mapping;

  ReplaceCollectionBuilder(TypedPropertyDescriptor<Collection<RS>> sourceProperty,
      TypedPropertyDescriptor<Collection<RD>> destProperty, Mapping<S, D> mapping) {
    super();
    this.sourceProperty = sourceProperty;
    this.destProperty = destProperty;
    this.mapping = mapping;
  }

  /**
   * Transforms the items in the collection held by the selected field by applying the specified transform function on
   * each item. <b>Note: The transform function must check the value for <code>null</code> itself. Use {@link
   * #withSkipWhenNull(Transform)} to skip on <code>null</code> items.</b>
   *
   * @param transformation The transform function.
   * @return Returns the {@link Mapping} for further mapping configuration.
   */
  public Mapping<S, D> with(Transform<RS, RD> transformation) {
    denyNull("tranformation", transformation);
    ReplaceCollectionTransformation<RS, RD> replace = new ReplaceCollectionTransformation<>(mapping,
        sourceProperty.property, destProperty.property, transformation, false);
    mapping.addMapping(sourceProperty.property, destProperty.property, replace);
    return mapping;
  }

  /**
   * Transforms the items in the collection held by the selected field by applying the specified transform function on
   * each item if the item is not <code>null</code>. <b>This method
   * skips the execution of the transform function if the source value is <code>null</code>.</b>
   *
   * @param transformation The transform function.
   * @return Returns the {@link Mapping} for further mapping configuration.
   */
  public Mapping<S, D> withSkipWhenNull(Transform<RS, RD> transformation) {
    denyNull("tranformation", transformation);
    ReplaceCollectionTransformation<RS, RD> replace = new ReplaceCollectionTransformation<>(mapping,
        sourceProperty.property, destProperty.property, transformation, true);
    mapping.addMapping(sourceProperty.property, destProperty.property, replace);
    return mapping;
  }
}
