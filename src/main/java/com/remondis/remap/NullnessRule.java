package com.remondis.remap;

/**
 * A nullness rule that a mapping configuration may violate. Every constant describes a concrete runtime behaviour of
 * the mapping that contradicts the nullness declared on the involved properties.
 *
 * @see NullnessRules
 */
public enum NullnessRule {

  /**
   * The mapping is skipped if the source value is <code>null</code>, so the destination property is never written and
   * keeps its default value <code>null</code>, although it is declared to be non-null.
   */
  UNWRITTEN_DESTINATION("The mapping is skipped if the source value is null, so the destination property is never "
      + "written and keeps its default value null.", "Declare the destination property as @Nullable, make the source property non-null, or make sure the "
          + "destination property has a non-null default value."),

  /**
   * The mapper is configured to write <code>null</code> values, so <code>null</code> is written to a destination
   * property that is declared to be non-null.
   */
  NULL_WRITTEN_TO_NON_NULL("The mapper is configured to write null if the source value is null, so null is written to "
      + "the destination property.", "Declare the destination property as @Nullable, make the source property non-null, or remove "
          + "writeNullIfSourceIsNull() from the mapping configuration."),

  /**
   * The mapper is configured to write <code>null</code> values into a destination property of primitive type. The
   * setter invocation fails with a {@link MappingException} as soon as the source value is <code>null</code>.
   */
  NULL_WRITTEN_TO_PRIMITIVE("The mapper is configured to write null if the source value is null, but the destination "
      + "property is of primitive type. Writing null to it fails with a MappingException as soon as the source value "
      + "is null.", "Make the source property non-null, use a non-primitive destination property, or remove "
          + "writeNullIfSourceIsNull() from the mapping configuration."),

  /**
   * The elements of a mapped collection are declared to be nullable. ReMap denies <code>null</code> elements while
   * mapping collections.
   */
  NULLABLE_COLLECTION_ELEMENT("The elements of the mapped collection are declared as @Nullable, but ReMap denies null "
      + "elements while mapping collections. The mapping fails as soon as the collection contains a null element.", "Declare the collection elements as non-null or make sure the source collection never contains null elements."),

  /**
   * The keys or values of a mapped map are declared to be nullable and are converted by a registered mapper. A mapper
   * denies <code>null</code> input.
   */
  NULLABLE_MAP_ENTRY("The keys or values of the mapped map are declared as @Nullable and are converted by a registered "
      + "mapper. A mapper denies null input, so the mapping fails as soon as the map contains a null key or value.", "Declare the keys and values as non-null or make sure the source map never contains null keys or values.");

  private final String description;

  private final String hint;

  NullnessRule(String description, String hint) {
    this.description = description;
    this.hint = hint;
  }

  /**
   * @return Returns the description of the runtime behaviour that violates the declared nullness.
   */
  public String getDescription() {
    return description;
  }

  /**
   * @return Returns a hint describing how the violation can be resolved.
   */
  public String getHint() {
    return hint;
  }

}
