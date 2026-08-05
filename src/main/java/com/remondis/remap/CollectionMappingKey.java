package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.util.function.Function;

/**
 * Holds key extractor functions for matching source and destination elements in collections during
 * {@link Mapper#map(Object, Object)} (mapInto) operations.
 *
 * <p>
 * When mapping into an existing destination object that contains collections, elements from the source collection
 * need to be matched to existing elements in the destination collection. This class stores the key extractor
 * functions that extract a comparable key from both source and destination elements to establish this matching.
 * </p>
 *
 * @param <S> The source element type
 * @param <D> The destination element type
 * @param <K> The key type used for matching
 */
class CollectionMappingKey<S, D, K> {

  private final Function<S, K> sourceKeyExtractor;
  private final Function<D, K> destinationKeyExtractor;

  CollectionMappingKey(Function<S, K> sourceKeyExtractor, Function<D, K> destinationKeyExtractor) {
    denyNull("sourceKeyExtractor", sourceKeyExtractor);
    denyNull("destinationKeyExtractor", destinationKeyExtractor);
    this.sourceKeyExtractor = sourceKeyExtractor;
    this.destinationKeyExtractor = destinationKeyExtractor;
  }

  Function<S, K> getSourceKeyExtractor() {
    return sourceKeyExtractor;
  }

  Function<D, K> getDestinationKeyExtractor() {
    return destinationKeyExtractor;
  }

}
