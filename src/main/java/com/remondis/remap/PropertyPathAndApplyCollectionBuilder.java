package com.remondis.remap;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.function.Function;

import com.remondis.propertypath.api.PropertyPath;

/**
 * This builder allows specifying a transformation function that is applied to a non-null result of a property path
 * evaluation. The function will only be applied, if the evaluation of the property path returns a non-null value.
 * The function may itself return <code>null</code>. In this case the property path is considered to have no value.
 *
 * @param <S> The source type of the mapper.
 * @param <D> The destination type of the mapper.
 * @param <RS> The type of the source field to map.
 * @param <X> The type of the value returned by the property path.
 * @param <RD> The type of the destination field.
 * @param <E> The exception that may be thrown by a property path at evaluation time.
 */
public class PropertyPathAndApplyCollectionBuilder<S, D, RD, X, RS, E extends Exception> {

  private MappingConfiguration<S, D> mapping;
  private TypedPropertyDescriptor<Collection<RS>> sourceProperty;
  private TypedPropertyDescriptor<Collection<RD>> destProperty;
  private PropertyPath<X, RS, E> propertyPath;

  PropertyPathAndApplyCollectionBuilder(MappingConfiguration<S, D> mapping,
      TypedPropertyDescriptor<Collection<RS>> sourceProperty, TypedPropertyDescriptor<Collection<RD>> destProperty,
      PropertyPath<X, RS, E> propertyPath) {
    this.mapping = mapping;
    this.sourceProperty = sourceProperty;
    this.destProperty = destProperty;
    this.propertyPath = propertyPath;
  }

  /**
   * Specifies a function that is applied to the evaluation result of the property path. <b>The function will only be
   * applied in case the property path evaluates to a non-null value.</b>
   *
   * @param transformation The function to transform the property path result value.
   * @return Returns the {@link MappingConfiguration} instance.
   */
  public MappingConfiguration<S, D> apply(Function<X, RD> transformation) {
    requireNonNull(transformation, "Transformation must not be null");
    PropertyPathCollectionTransformation<RS, X, RD> replace = new PropertyPathCollectionTransformation<RS, X, RD>(
        mapping, sourceProperty.property, destProperty.property, propertyPath, transformation);
    mapping.addMapping(sourceProperty.property, destProperty.property, replace);
    return mapping;
  }
}
