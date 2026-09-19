package com.remondis.remap;

import java.util.Optional;

/**
 * The nullness rules of ReMap's mapping operations. This class is a pure function of the nullness of the involved
 * properties and the configuration of the mapping operation - it does neither use reflection nor does it know how the
 * nullness was determined.
 *
 * <p>
 * The rules are deliberately kept free of any dependency on the way the facts are obtained, so that a static analysis
 * reading the same facts from an abstract syntax tree produces exactly the same verdicts as the runtime validation
 * performed while building a mapper.
 * </p>
 *
 * <p>
 * <b>Rules that cannot be evaluated at runtime:</b> The transformation functions of the <code>replace</code> and
 * <code>set</code> operations are lambdas or method references. Their nullness is not available reflectively, so the
 * corresponding rules are not part of this class. Those contracts are covered by the signatures of the builder
 * methods instead and are therefore checked by the compiler.
 * </p>
 */
public final class NullnessRules {

  private NullnessRules() {
  }

  /**
   * Evaluates the nullness rules of a <code>reassign</code> operation and of the implicit mappings, which use the same
   * transformation.
   *
   * @param source The nullness of the source property.
   * @param destination The nullness of the destination property.
   * @param destinationPrimitive Whether the destination property is of primitive type.
   * @param writeNullIfSourceIsNull Whether the mapper was configured with
   *        {@link MappingConfiguration#writeNullIfSourceIsNull()}.
   * @param destinationHasNonNullDefault Whether the destination property holds a non-null value in a freshly created
   *        destination object. If so, skipping the mapping does not leave a <code>null</code> value behind.
   * @return Returns the violated rule or an empty {@link Optional} if the operation is sound.
   */
  public static Optional<NullnessRule> reassign(Nullness source, Nullness destination, boolean destinationPrimitive,
      boolean writeNullIfSourceIsNull, boolean destinationHasNonNullDefault) {
    if (source != Nullness.NULLABLE) {
      return Optional.empty();
    }
    if (writeNullIfSourceIsNull) {
      if (destinationPrimitive) {
        return Optional.of(NullnessRule.NULL_WRITTEN_TO_PRIMITIVE);
      }
      if (destination == Nullness.NON_NULL) {
        return Optional.of(NullnessRule.NULL_WRITTEN_TO_NON_NULL);
      }
      return Optional.empty();
    }
    // Without writeNullIfSourceIsNull the destination property is not written at all. A primitive destination keeps
    // its primitive default value, which is not a nullness violation.
    if (destinationPrimitive) {
      return Optional.empty();
    }
    return unwrittenDestination(destination, destinationHasNonNullDefault);
  }

  /**
   * Evaluates the nullness rules of operations that skip the mapping if the source value is <code>null</code>. This
   * applies to <code>replace</code> operations declared with
   * {@link ReplaceBuilder#withSkipWhenNull(java.util.function.Function)}, to the collection variants of the
   * <code>replace</code> operation and to property path operations, which are null-friendly by design.
   *
   * @param source The nullness of the source property.
   * @param destination The nullness of the destination property.
   * @param destinationHasNonNullDefault Whether the destination property holds a non-null value in a freshly created
   *        destination object.
   * @return Returns the violated rule or an empty {@link Optional} if the operation is sound.
   */
  public static Optional<NullnessRule> skipWhenNull(Nullness source, Nullness destination,
      boolean destinationHasNonNullDefault) {
    if (source != Nullness.NULLABLE) {
      return Optional.empty();
    }
    return unwrittenDestination(destination, destinationHasNonNullDefault);
  }

  /**
   * Evaluates the nullness rule for the elements of a mapped collection. ReMap denies <code>null</code> elements while
   * mapping collections, regardless of the conversion that is applied to the elements.
   *
   * @param element The nullness of the collection element type.
   * @return Returns the violated rule or an empty {@link Optional} if the operation is sound.
   */
  public static Optional<NullnessRule> collectionElement(Nullness element) {
    if (element == Nullness.NULLABLE) {
      return Optional.of(NullnessRule.NULLABLE_COLLECTION_ELEMENT);
    }
    return Optional.empty();
  }

  /**
   * Evaluates the nullness rule for the keys and values of a mapped map. In contrast to collections, <code>null</code>
   * keys and values are only denied if they are converted by a registered mapper, because a mapper denies
   * <code>null</code> input. If the entries are mapped by reference, <code>null</code> is passed through.
   *
   * @param entry The nullness of the key or value type of the map.
   * @param mapperRequired Whether the key or value is converted by a registered mapper.
   * @return Returns the violated rule or an empty {@link Optional} if the operation is sound.
   */
  public static Optional<NullnessRule> mapEntry(Nullness entry, boolean mapperRequired) {
    if (entry == Nullness.NULLABLE && mapperRequired) {
      return Optional.of(NullnessRule.NULLABLE_MAP_ENTRY);
    }
    return Optional.empty();
  }

  private static Optional<NullnessRule> unwrittenDestination(Nullness destination,
      boolean destinationHasNonNullDefault) {
    if (destination == Nullness.NON_NULL && !destinationHasNonNullDefault) {
      return Optional.of(NullnessRule.UNWRITTEN_DESTINATION);
    }
    return Optional.empty();
  }

}
