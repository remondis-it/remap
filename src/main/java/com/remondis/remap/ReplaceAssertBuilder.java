package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

/**
 * Builder to assert a replace operation on a {@link Mapper} object using {@link AssertMapping}.
 *
 * @param <S>
 *        The source type
 * @param <D>
 *        The destination type
 * @param <RS>
 *        The type of the source field
 * @param <RD>
 *        The type of the destination field
 *
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
   * Specifies the transformation function that will be checked against null input.
   *
   * @param transformation
   *        The transformation to test.
   * @return Returns the {@link AssertMapping} for further configuration.
   */
  public AssertMapping<S, D> andTest(Transform<RD, RS> transformation) {
    denyNull("tranfromation", transformation);
    ReplaceTransformation<RD, RS> replace = new ReplaceTransformation<RD, RS>(asserts.getMapping(),
                                                                              sourceProperty.property,
                                                                              destProperty.property, transformation,
                                                                              false);
    asserts.addAssertion(replace);
    return asserts;
  }

  /**
   * Specifies the transform operation to be skipped when null. In this case the transformation function will not be
   * tested. In a future release this method may allow to test the transformation function.
   *
   * @param transformation
   *        The transformation function
   * @return Returns the {@link AssertMapping} for further configuration.
   */
  public AssertMapping<S, D> andTestButSkipWhenNull(Transform<RD, RS> transformation) {
    denyNull("tranfromation", transformation);
    ReplaceTransformation<RD, RS> replace = new ReplaceTransformation<RD, RS>(asserts.getMapping(),
                                                                              sourceProperty.property,
                                                                              destProperty.property, transformation,
                                                                              true);
    asserts.addAssertion(replace);
    return asserts;
  }
}
