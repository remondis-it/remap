package com.remondis.remap;

/**
 * An implementation to generalize a {@link Mapper} using {@link InternalMapper}.
 *
 * @param <S> the source type
 * @param <D> the destination type.
 */
class MapperAdapter<S, D> implements InternalMapper<S, D> {

  private Mapper<S, D> mapper;

  public MapperAdapter(Mapper<S, D> mapper) {
    super();
    this.mapper = mapper;
  }

  @Override
  public D map(S source, D destination) {
    return mapper.map(source, destination);
  }

  @Override
  public D map(S source) {
    return mapper.map(source);
  }

  @Override
  public Projection<S, D> getProjection() {
    Class<S> source = mapper.getMapping()
        .getSource();
    Class<D> destination = mapper.getMapping()
        .getDestination();
    return new Projection<>(source, destination);
  }

  Mapper<S, D> getMapper() {
    return mapper;
  }

}
