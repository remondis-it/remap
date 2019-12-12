package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.util.function.Function;

import com.remondis.propertypath.api.PropertyPath;

/**
 * Builder to assert a replace operation on a {@link Mapper} object using {@link AssertConfiguration}.
 *
 * @param <S> The source type
 * @param <D> The destination type
 * @param <RS> The type of the source field
 * @param <RD> The type of the destination field
 * @author schuettec
 */
public class ReplaceAssertBuilder<S, D, RD, RS> {

  private TypedPropertyDescriptor<RS> sourceProperty;
  private TypedPropertyDescriptor<RD> destProperty;
  private AssertConfiguration<S, D> asserts;

  ReplaceAssertBuilder(TypedPropertyDescriptor<RS> sourceProperty, TypedPropertyDescriptor<RD> destProperty,
      AssertConfiguration<S, D> asserts) {
    super();
    this.sourceProperty = sourceProperty;
    this.destProperty = destProperty;
    this.asserts = asserts;
  }

  /**
   * Expects the mapping to evaluate the exact property path that was specified.
   *
   * @param propertyPath The expected property path.
   * @return Returns the {@link AssertConfiguration} for further configuration.
   */
  public <E extends Exception> AssertConfiguration<S, D> withPropertyPath(PropertyPath<RD, RS, E> propertyPath) {
    denyNull("propertyPath", propertyPath);
    PropertyPathTransformation<RS, RD, RD> replace = new PropertyPathTransformation<RS, RD, RD>(asserts.getMapping(),
        sourceProperty.property, destProperty.property, propertyPath);
    asserts.addAssertion(replace);
    return asserts;
  }

  /**
   * Expects the mapping to evaluate the exact property path that was specified.
   *
   * @param propertyPath The expected property path.
   * @return Returns the {@link AssertConfiguration} for further configuration.
   */
  public AssertConfiguration<S, D> withPropertyPathAndTransformation(PropertyPath<?, RS, ?> propertyPath) {
    denyNull("propertyPath", propertyPath);
    PropertyPathTransformation<RS, ?, ?> replace = new PropertyPathTransformation<>(asserts.getMapping(),
        sourceProperty.property, destProperty.property, propertyPath, Function.identity());
    asserts.addAssertion(replace);
    return asserts;
  }

  /**
   * Expects the mapping to <b>not</b> skip the transform function on <code>null</code> input. The specified transform
   * function will be checked against <code>null</code> input.
   *
   * <p>
   * Note: This method cannot reliably check that the specified function is actually the function that was configured on
   * the mapping. This method only verifies the skip-on-null behaviour and performs a <code>null</code> check on the
   * specified function.
   * </p>
   *
   * @param transformation The transformation to test.
   * @return Returns the {@link AssertConfiguration} for further configuration.
   */
  public AssertConfiguration<S, D> andTest(Function<RS, RD> transformation) {
    denyNull("tranfromation", transformation);
    ReplaceTransformation<RS, RD> replace = new ReplaceTransformation<>(asserts.getMapping(), sourceProperty.property,
        destProperty.property, transformation, false);
    asserts.addAssertion(replace);
    return asserts;
  }

  /**
   * Expects the mapping to skip the transform function on <code>null</code> input.
   *
   * @return Returns the {@link AssertConfiguration} for further configuration.
   */
  public AssertConfiguration<S, D> andSkipWhenNull() {
    ReplaceTransformation<RD, RS> replace = new ReplaceTransformation<RD, RS>(asserts.getMapping(),
        sourceProperty.property, destProperty.property, null, true);
    asserts.addAssertion(replace);
    return asserts;
  }
}
