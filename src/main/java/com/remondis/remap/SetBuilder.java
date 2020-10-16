package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Builds a replace operation.
 *
 * @param <S> The source type.
 * @param <D> The destination type.
 * @param <RD> The destination field type.
 *
 */
public class SetBuilder<S, D, RD> {

  static final String TRANSFORM = "transform";

  private TypedPropertyDescriptor<RD> destProperty;
  private MappingConfiguration<S, D> mapping;

  SetBuilder(TypedPropertyDescriptor<RD> destProperty, MappingConfiguration<S, D> mapping) {
    super();
    this.destProperty = destProperty;
    this.mapping = mapping;
  }

  /**
   * Sets a value to the specified destination field supplied by a custom value function. The specified function will be
   * applied to the source object reference.
   *
   * <b>Note: It is not necessary to implement a null-safe function. The specified function will never be called with a
   * <code>null</code></b> argument.
   *
   * @param valueSupplier The value supplier that requires a reference to the whole source object.
   * @return Returns the {@link MappingConfiguration} for further mapping configuration.
   */
  public MappingConfiguration<S, D> with(Function<S, RD> valueSupplier) {
    denyNull("valueSupplier", valueSupplier);
    SetTransformation<S, D, RD> setTransformation = new SetTransformation<>(mapping, destProperty.property,
        valueSupplier);
    mapping.addDestinationMapping(destProperty.property, setTransformation);
    return mapping;
  }

  /**
   * Sets a value to the specified destination field supplied by a custom value supplier.
   *
   * @param valueSupplier The value supplier that requires a reference to the whole source object.
   * @return Returns the {@link MappingConfiguration} for further mapping configuration.
   */
  public MappingConfiguration<S, D> with(Supplier<RD> valueSupplier) {
    denyNull("valueSupplier", valueSupplier);
    SetSupplierTransformation<S, D, RD> setTransformation = new SetSupplierTransformation<>(mapping,
        destProperty.property, valueSupplier);
    mapping.addDestinationMapping(destProperty.property, setTransformation);
    return mapping;
  }

  /**
   * Sets the specified value to the specified destination field.
   *
   * @param value The value to set.
   * @return Returns the {@link MappingConfiguration} for further mapping configuration.
   */
  public MappingConfiguration<S, D> with(RD value) {
    denyNull("value", value);
    SetValueTransformation<S, D, RD> setTransformation = new SetValueTransformation<>(mapping, destProperty.property,
        value);
    mapping.addDestinationMapping(destProperty.property, setTransformation);
    return mapping;
  }
}
