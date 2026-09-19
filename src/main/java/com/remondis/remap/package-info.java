/**
 * ReMap - A declarative mapping library for converting objects field by field.
 *
 * <p>
 * This package is {@link org.jspecify.annotations.NullMarked}: unless a type usage is explicitly annotated with
 * {@link org.jspecify.annotations.Nullable}, it is not expected to be <code>null</code>. Tools like NullAway, the
 * Checker Framework, IntelliJ IDEA and the Kotlin compiler evaluate these annotations and report violations at compile
 * time.
 * </p>
 *
 * <p>
 * The <code>null</code> contract of the transformation functions is carried by the type variables of the mapping
 * operations: the source field type <code>RS</code> and the destination field type <code>RD</code> are inferred from
 * the field selectors, so they already reflect the nullness declared on the selected getters. A transformation
 * function specified with {@link com.remondis.remap.ReplaceBuilder#with(java.util.function.Function)} must therefore
 * accept the source field type as declared - including <code>null</code>, if the source field is
 * {@link org.jspecify.annotations.Nullable}. Operations that are skipped for <code>null</code> input, like
 * {@link com.remondis.remap.ReplaceBuilder#withSkipWhenNull(java.util.function.Function)}, declare the non-null
 * projection of the source field type instead, because the function is never called with <code>null</code>.
 * </p>
 */
@NullMarked
package com.remondis.remap;

import org.jspecify.annotations.NullMarked;
