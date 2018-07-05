package com.remondis.remap;

/**
 * Builder to assert a replace operation on a {@link Mapper} object using {@link AssertMapping}.
 *
 * @param <S> The source type
 * @param <D> The destination type
 * @param <RD> The type of the destination field
 * @author schuettec
 */
public class SetAssertBuilder<S, D, RD> {

  private TypedPropertyDescriptor<RD> destProperty;
  private AssertMapping<S, D> asserts;

  SetAssertBuilder(TypedPropertyDescriptor<RD> destProperty, AssertMapping<S, D> asserts) {
    super();
    this.destProperty = destProperty;
    this.asserts = asserts;
  }

  /**
   * Expects a set-mapping with a function providing a value.
   *
   * @return Returns the {@link AssertMapping} for further configuration.
   */
  public AssertMapping<S, D> withFunction() {
    SetTransformation<S, D, RD> replace = new SetTransformation<>(asserts.getMapping(), destProperty.property, null);
    asserts.addAssertion(replace);
    return asserts;
  }

  /**
   * Expects a set-mapping with a value supplier.
   *
   * @return Returns the {@link AssertMapping} for further configuration.
   */
  public AssertMapping<S, D> withSupplier() {
    SetSupplierTransformation<S, D, RD> replace = new SetSupplierTransformation<>(asserts.getMapping(),
        destProperty.property, null);
    asserts.addAssertion(replace);
    return asserts;
  }

  /**
   * Expects a set-mapping with a value.
   *
   * @return Returns the {@link AssertMapping} for further configuration.
   */
  public AssertMapping<S, D> withValue() {
    SetValueTransformation<S, D, RD> replace = new SetValueTransformation<>(asserts.getMapping(), destProperty.property,
        null);
    asserts.addAssertion(replace);
    return asserts;
  }
}
