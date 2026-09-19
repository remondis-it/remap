package com.remondis.remap;

/**
 * The nullness of a type usage as declared by <a href="https://jspecify.dev">JSpecify</a> annotations.
 *
 * <p>
 * This enumeration is the input of the {@link NullnessRules} and decouples the rules from the way the nullness was
 * determined. At runtime the nullness is read reflectively from the accessor methods of a property, a static analysis
 * would read the same information from the abstract syntax tree.
 * </p>
 */
public enum Nullness {

  /**
   * The type usage is annotated with <code>org.jspecify.annotations.Nullable</code> and may hold <code>null</code>.
   */
  NULLABLE,

  /**
   * The type usage is known to exclude <code>null</code>. This is the case for primitive types, for type usages
   * annotated with <code>org.jspecify.annotations.NonNull</code> and for unannotated type usages within a
   * <code>org.jspecify.annotations.NullMarked</code> scope.
   */
  NON_NULL,

  /**
   * The nullness of the type usage is unspecified, because it is not covered by a
   * <code>org.jspecify.annotations.NullMarked</code> scope or because it cannot be determined, for example for type
   * variables. Unspecified nullness never produces a violation.
   */
  UNSPECIFIED;

}
