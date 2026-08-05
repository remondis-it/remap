package com.remondis.remap;

import static com.remondis.remap.ReflectionUtil.getCollector;
import static java.util.Objects.isNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This class defines a reusable mapper object to perform multiple mappings for the configured object types.
 *
 * @param <S> The source type
 * @param <D> The destination type
 * @author schuettec
 */
public class Mapper<S, D> {

  private MappingConfiguration<S, D> mapping;

  Mapper(MappingConfiguration<S, D> mapping) {
    super();
    this.mapping = mapping;
  }

  MappingConfiguration<S, D> getMapping() {
    return mapping;
  }

  /**
   * Performs the mapping from the source to destination type.
   *
   * @param source The source object to map to a new destination object.
   * @return Returns a newly created destination object.
   */
  public D map(S source) {
    return mapping.map(source);
  }

  /**
   * Performs the mapping from the source into a specified destination object while overwriting fields in the
   * destination object if affected by the mapping configuration.
   *
   * <p>
   * For collection properties, elements are matched between source and destination collections using key extractor
   * functions registered via
   * {@link MappingConfiguration#useMapper(Mapper, java.util.function.Function, java.util.function.Function)}.
   * Matched elements are mapped into each other (preserving destination-only fields), unmatched source elements
   * are mapped to new destination objects, and unmatched destination elements are discarded.
   * </p>
   *
   * <p>
   * If no key extractors are registered for a collection's element types, the collection is replaced entirely
   * (existing behavior).
   * </p>
   *
   * <p>
   * <b>Note on records:</b> If the destination type is a Java Record, a <b>new record instance</b> is returned
   * because records are immutable. The component values of the specified destination record serve as defaults for
   * all fields not covered by the mapping. Make sure to use the returned instance.
   * </p>
   *
   * @param source The source object to map to a new destination object.
   * @param destination The destination object to map into. Fields affected by the mapping will be overwritten.
   * @return Returns the specified destination object, or a new instance if the destination type is a record.
   */
  public D map(S source, D destination) {
    return mapping.map(source, destination);
  }

  /**
   * Performs the mapping for the specified {@link Collection}.
   *
   * @param source The source collection to map to a new collection of destination objects.
   * @return Returns a newly created collection of destination objects. The type of the resulting collection is either
   *         {@link List} or {@link Set} depending on the specified type.
   */
  public Collection<D> map(Collection<? extends S> source) {
    return _mapCollection(source);
  }

  /**
   * Performs the mapping for the specified {@link List}.
   *
   * @param source The source collection to map to a new collection of destination objects.
   * @return Returns a newly created list of destination objects.
   */
  public List<D> map(List<? extends S> source) {
    return (List<D>) _mapCollection(source);
  }

  /**
   * Performs the mapping for the specified {@link Set}.
   *
   * @param source The source collection to map to a new collection of destination objects.
   * @return Returns a newly set list of destination objects.
   */
  public Set<D> map(Set<? extends S> source) {
    return (Set<D>) _mapCollection(source);
  }

  /**
   * Performs the mapping for the elements provided by the specified {@link Iterable} .
   *
   * @param iterable The source iterable to be mapped to a new {@link List} of destination objects.
   * @return Returns a newly set list of destination objects.
   */
  public List<D> map(Iterable<? extends S> iterable) {
    Stream<? extends S> stream = StreamSupport.stream(iterable.spliterator(), false);
    return stream.map(this::map)
        .collect(Collectors.toList());
  }

  /**
   * Performs the mapping from the source to destination type if the source value is <b>non-null</b>. If the source
   * value is <code>null</code> this method returns <code>null</code>.
   *
   * @param source The source object to map to a new destination object. May be <code>null</code>.
   * @return Returns a newly created destination object or <code>null</code> if the input value is <code>null</code>.
   */
  public D mapOptional(S source) {
    return mapOrDefault(source, null);
  }

  /**
   * Performs the mapping from the source to destination type if the source value is <b>non-null</b>. If the source
   * value is <code>null</code> this method returns the specified default value.
   *
   * @param source The source object to map to a new destination object. May be <code>null</code>.
   * @param defaultValue The default value to return if the input is <code>null</code>.
   * @return Returns a newly created destination object or the default value if the input value is <code>null</code>.
   */
  public D mapOrDefault(S source, D defaultValue) {
    if (isNull(source)) {
      return defaultValue;
    } else {
      return mapping.map(source);
    }
  }

  @SuppressWarnings("unchecked")
  private Collection<D> _mapCollection(Collection<? extends S> source) {
    return (Collection<D>) source.stream()
        .map(this::map)
        .collect(getCollector(source));
  }

  @Override
  public String toString() {
    return mapping.toString();
  }

  /**
   * @return Returns the {@link MappingModel} for this mapping.
   */
  public MappingModel<S, D> getMappingModel() {
    return new MappingModel<>(getMapping());
  }

}
