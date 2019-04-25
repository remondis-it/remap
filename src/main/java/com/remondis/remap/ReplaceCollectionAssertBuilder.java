package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.util.Collection;
import java.util.function.Function;

import com.remondis.propertypath.api.PropertyPath;

/**
 * Builder to assert a replace operation on a {@link Mapper} object using {@link AssertMapping}.
 *
 * @param <S> The source type
 * @param <D> The destination type
 * @param <RS> The type of the source field
 * @param <RD> The type of the destination field
 * @author schuettec
 */
public class ReplaceCollectionAssertBuilder<S, D, RD, RS> {

  private TypedPropertyDescriptor<Collection<RS>> sourceProperty;
  private TypedPropertyDescriptor<Collection<RD>> destProperty;
  private AssertMapping<S, D> asserts;

  ReplaceCollectionAssertBuilder(TypedPropertyDescriptor<Collection<RS>> sourceProperty,
      TypedPropertyDescriptor<Collection<RD>> destProperty, AssertMapping<S, D> asserts) {
    super();
    this.sourceProperty = sourceProperty;
    this.destProperty = destProperty;
    this.asserts = asserts;
  }

  /**
   * Expects the mapping to evaluate the exact property path that was specified.
   *
   * @param propertyPath The expected property path.
   * @return Returns the {@link AssertMapping} for further configuration.
   */
  public <E extends Exception> AssertMapping<S, D> withPropertyPath(PropertyPath<RD, RS, E> propertyPath) {
    denyNull("propertyPath", propertyPath);
    PropertyPathCollectionTransformation<RS, RD, RD> replace = new PropertyPathCollectionTransformation<RS, RD, RD>(
        asserts.getMapping(), sourceProperty.property, destProperty.property, propertyPath);
    asserts.addAssertion(replace);
    return asserts;
  }

  /**
   * Expects the mapping to evaluate the exact property path that was specified.
   *
   * @param propertyPath The expected property path.
   * @return Returns the {@link AssertMapping} for further configuration.
   */
  public AssertMapping<S, D> withPropertyPathAndTransformation(PropertyPath<?, RS, ?> propertyPath) {
    denyNull("propertyPath", propertyPath);
    PropertyPathCollectionTransformation<RS, ?, ?> replace = new PropertyPathCollectionTransformation<>(
        asserts.getMapping(), sourceProperty.property, destProperty.property, propertyPath, Function.identity());
    asserts.addAssertion(replace);
    return asserts;
  }

  /**
   * Specifies the transformation function that will be checked against null input.
   *
   * @param transformation The transformation to test.
   * @return Returns the {@link AssertMapping} for further configuration.
   */
  public AssertMapping<S, D> andTest(Function<RS, RD> transformation) {
    denyNull("tranfromation", transformation);
    ReplaceCollectionTransformation<RS, RD> replace = new ReplaceCollectionTransformation<>(asserts.getMapping(),
        sourceProperty.property, destProperty.property, transformation, false);
    asserts.addAssertion(replace);
    return asserts;
  }

  /**
   * Specifies the transform operation to be skipped when null. In this case the transformation function will not be
   * tested. In a future release this method may allow to test the transformation function.
   *
   * @return Returns the {@link AssertMapping} for further configuration.
   */
  public AssertMapping<S, D> andSkipWhenNull() {
    ReplaceCollectionTransformation<RS, RD> replace = new ReplaceCollectionTransformation<>(asserts.getMapping(),
        sourceProperty.property, destProperty.property, null, true);
    asserts.addAssertion(replace);
    return asserts;
  }
}
