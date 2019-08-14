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

  private Mapping<S, D> mapping;

  Mapper(Mapping<S, D> mapping) {
    super();
    this.mapping = mapping;
  }

  Mapping<S, D> getMapping() {
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
   * @param source The source object to map to a new destination object.
   * @param destination The destination object to map into. Field affected by the mapping will be overwritten.
   * @return Returns the specified destination object.
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
   * Creates a new {@link Mapping} for derived source/destination classes. The mapping configuration of this
   * {@link Mapper} is used for the resulting {@link Mapping}. <b>Note: Mapping for additional fields within the
   * derived source/destination classes must be declared on the returned {@link Mapper} in order to get a valid
   * mapping.</b>
   *
   * <h2>What is copied from parent mapper?</h2>
   * The resulting {@link Mapping} has the same settings like the parent mapping and they can be reused.
   * <ul>
   * <li>all registered mappers and type mappings</li>
   * <li>all mapping transformations</li>
   * <li>setting for omitting all source fields</li>
   * <li>setting for omitting all destination fields</li>
   * <li>setting for suppressing generation of implicit mappings</li>
   *
   * </ul>
   *
   * @param <SD> The new source type. Must be a subclass of the source type used by this {@link Mapper}.
   * @param <DD> The new destination type. Must be a subclass of the destination type used by this {@link Mapper}.
   * @param derivedSourceType The new source type. Must be a subclass of the source type used by this {@link Mapper}.
   * @param derivedDestinationType The new destination type. Must be a subclass of the destination type used by this
   *        {@link Mapper}.
   * @return Returns a new {@link Mapping} that uses the parent mapping configuration for the mapping of subclasses.
   */
  public <SD extends S, DD extends D> Mapping<SD, DD> derive(Class<SD> derivedSourceType,
      Class<DD> derivedDestinationType) {
    return new Mapping<>(derivedSourceType, derivedDestinationType, this.mapping);
  }

}
