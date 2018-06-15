package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.util.function.Function;

/**
 * Builder to assert a replace operation on a {@link Mapper} object using {@link AssertMapping}.
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
  private AssertMapping<S, D> asserts;

  ReplaceAssertBuilder(TypedPropertyDescriptor<RS> sourceProperty, TypedPropertyDescriptor<RD> destProperty,
      AssertMapping<S, D> asserts) {
    super();
    this.sourceProperty = sourceProperty;
    this.destProperty = destProperty;
    this.asserts = asserts;
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
   * @return Returns the {@link AssertMapping} for further configuration.
   */
  public AssertMapping<S, D> andTest(Function<RS, RD> transformation) {
    denyNull("tranfromation", transformation);
    ReplaceTransformation<RS, RD> replace = new ReplaceTransformation<>(asserts.getMapping(), sourceProperty.property,
        destProperty.property, transformation, false);
    asserts.addAssertion(replace);
    return asserts;
  }

  /**
   * Expects the mapping to skip the transform function on <code>null</code> input.
   *
   * @return Returns the {@link AssertMapping} for further configuration.
   */
  public AssertMapping<S, D> andSkipWhenNull() {
    ReplaceTransformation<RD, RS> replace = new ReplaceTransformation<RD, RS>(asserts.getMapping(),
        sourceProperty.property, destProperty.property, null, true);
    asserts.addAssertion(replace);
    return asserts;
  }
}
