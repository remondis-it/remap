package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.remondis.propertypath.api.PropertyPath;

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
  private MappingConfiguration<S, D> mapping;

  ReplaceCollectionBuilder(TypedPropertyDescriptor<Collection<RS>> sourceProperty,
      TypedPropertyDescriptor<Collection<RD>> destProperty, MappingConfiguration<S, D> mapping) {
    super();
    this.sourceProperty = sourceProperty;
    this.destProperty = destProperty;
    this.mapping = mapping;
  }

  /**
   * Maps the items of the collections held by the source field by evaluating the specified property path. A property
   * path is useful for flattening objects and the easy handling of optional values in a Java Bean object graph. The
   * mapping will only be performed if the property path evaluates to a non-null value. Otherwise the mapping will be
   * skipped.
   *
   * <h2>What is a property path</h2>
   * <p>
   * A property path is a chain of get calls. Java Bean compliant get methods
   * as well as {@link List#get(int)} and {@link Map#get(Object)} are supported.
   * The get call chain evaluation and the null checks between the get calls are performed by the framework to support
   * optional fields while avoiding the need of implementing null checks. The property path only evaluates to a non-null
   * value, if all get calls return a non-null value.
   * </p>
   *
   * <p>
   * The property path will be evaluated during the mapping and every get-call is checked for a
   * <code>null</code> value. If a get call in the chain returns a <code>null</code> value, the whole evaluation returns
   * no value. In this case the mapping will be skipped.
   * </p>
   *
   * @param propertyPath A lambda function performing get calls on the specified object to declare the actual property
   *        path.
   *        <b>This is not a function operating on real object. So do not manipulate or calculate here!</b>
   * @return Returns the {@link MappingConfiguration} for further mapping configuration.
   */
  public <E extends Exception> MappingConfiguration<S, D> withPropertyPath(PropertyPath<RD, RS, E> propertyPath) {
    denyNull("propertyPath", propertyPath);
    PropertyPathCollectionTransformation<RS, RD, RD> replace = new PropertyPathCollectionTransformation<RS, RD, RD>(
        mapping, sourceProperty.property, destProperty.property, propertyPath);
    mapping.addMapping(sourceProperty.property, destProperty.property, replace);
    return mapping;
  }

  /**
   * Works exactly like {@link #withPropertyPath(PropertyPath)} but accepts a transformation function, that is to be
   * specified on the
   * returned builder. The function will be applied to the result of the property path evaluation, <b>but only if the
   * property path evaluates to a non-null</b> value. If the specified transform function itself returns
   * <code>null</code> the property path evaluates to no value.
   *
   * @param propertyPath A lambda function performing get calls on the specified object to declare the actual property
   *        path.
   *        <b>This is not a function operating on real object. So do not manipulate or calculate here!</b>
   * @return Returns the {@link PropertyPathAndApplyBuilder} to specify the transformation function.
   */
  public <X, E extends Exception> PropertyPathAndApplyCollectionBuilder<S, D, RD, X, RS, E> withPropertyPathAnd(
      PropertyPath<X, RS, E> propertyPath) {
    denyNull("propertyPath", propertyPath);
    return new PropertyPathAndApplyCollectionBuilder<S, D, RD, X, RS, E>(mapping, sourceProperty, destProperty,
        propertyPath);
  }

  /**
   * Transforms the items in the collection held by the selected field by applying the specified transform function on
   * each item. <b>Note: The transform function must check the value for <code>null</code> itself. Use {@link
   * #withSkipWhenNull(Function)} to skip on <code>null</code> items.</b>
   *
   * @param transformation The transform function.
   * @return Returns the {@link MappingConfiguration} for further mapping configuration.
   */
  public MappingConfiguration<S, D> with(Function<RS, RD> transformation) {
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
   * @return Returns the {@link MappingConfiguration} for further mapping configuration.
   */
  public MappingConfiguration<S, D> withSkipWhenNull(Function<RS, RD> transformation) {
    denyNull("tranformation", transformation);
    ReplaceCollectionTransformation<RS, RD> replace = new ReplaceCollectionTransformation<>(mapping,
        sourceProperty.property, destProperty.property, transformation, true);
    mapping.addMapping(sourceProperty.property, destProperty.property, replace);
    return mapping;
  }
}
