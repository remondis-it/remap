package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.util.Optional;
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
  private BiFunction<S, Optional<D>, D> conversionFunction;

  static class TypeMappingBuilder<S> {
    private Class<S> source;

    public TypeMappingBuilder(Class<S> source) {
      super();
      this.source = source;
    }

    public <D> TypeMappingFunctionBuilder<S, D> to(Class<D> destination) {
      denyNull("destination", destination);
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

    /**
     * Specifies a conversion function that performs the type mapping. The function may support mapping into a
     * destination object if specified.
     *
     * @param conversionFunction The conversion function that performs the type conversion.
     * @return Returns the {@link TypeMapping} for use within a {@link Mapping} configuration.
     */
    public TypeMapping<S, D> applying(BiFunction<S, Optional<D>, D> conversionFunction) {
      denyNull("conversionFunction", conversionFunction);
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
    denyNull("source", source);
    return new TypeMappingBuilder<>(source);
  }

  TypeMapping(Class<S> source, Class<D> destination, BiFunction<S, Optional<D>, D> conversionFunction) {
    super();
    this.source = source;
    this.destination = destination;
    this.conversionFunction = conversionFunction;
  }

  @Override
  public D map(S source, D destination) {
    return conversionFunction.apply(source, Optional.of(destination));
  }

  @Override
  public D map(S source) {
    return conversionFunction.apply(source, Optional.empty());
  }

  @Override
  public Projection<S, D> getProjection() {
    return new Projection<>(source, destination);
  }

}
