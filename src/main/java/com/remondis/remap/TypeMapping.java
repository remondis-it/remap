package com.remondis.remap;

import static java.util.Objects.requireNonNull;

import java.util.function.BiFunction;

/**
 * A type mapping wraps a function that maps one type into another. This mapping can be used to define a global type
 * mapping on a {@link Mapping}. Apart from Java Bean mappers, that support a field by field mapping a
 * {@link TypeMapping} can be useful if a type conversion occurs very often in a mapping. A {@link TypeMapping} may
 * reduce the number of replace-operations needed to define a mapping.
 *
 *
 * @param <S> The source type
 * @param <D> The destination type.
 */
public final class TypeMapping<S, D> implements InternalMapper<S, D> {

  private Class<S> source;
  private Class<D> destination;
  private BiFunction<S, D, D> conversionFunction;

  static class TypeMappingBuilder<S> {
    private Class<S> source;

    public TypeMappingBuilder(Class<S> source) {
      super();
      this.source = source;
    }

    public <D> TypeMappingFunctionBuilder<S, D> to(Class<D> destination) {
      requireNonNull(destination, "Destination must not be null!");
      return new TypeMappingFunctionBuilder<>(source, destination);
    }

  }

  static class TypeMappingFunctionBuilder<S, D> {
    private Class<S> source;
    private Class<D> destination;

    TypeMappingFunctionBuilder(Class<S> source, Class<D> destination) {
      super();
      this.source = source;
      this.destination = destination;
    }

    public TypeMapping<S, D> applying(BiFunction<S, D, D> conversionFunction) {
      requireNonNull(destination, "Conversion function must not be null!");
      return new TypeMapping<>(source, destination, conversionFunction);
    }

  }

  /**
   * Specifies the source data type to map from.
   *
   * @param source
   *        the data source type.
   * @return Returns a {@link Types} object for further mapping configuration.
   */
  public static <S> TypeMappingBuilder<S> from(Class<S> source) {
    requireNonNull(source, "Source must not be null!");
    return new TypeMappingBuilder<>(source);
  }

  TypeMapping(Class<S> source, Class<D> destination, BiFunction<S, D, D> conversionFunction) {
    super();
    this.source = source;
    this.destination = destination;
  }

  @Override
  public D map(S source, D destination) {
    return conversionFunction.apply(source, destination);
  }

  @Override
  public Projection<S, D> getProjection() {
    return new Projection<>(source, destination);
  }

}
