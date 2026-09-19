package com.remondis.remap;

import static com.remondis.remap.ReassignTransformation.isCollection;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jspecify.annotations.Nullable;

/**
 * Validates the mapping configuration of a mapper against the nullness declared by the JSpecify annotations of the
 * involved properties. The validation is performed while the mapper is built, following the design principle of ReMap
 * to detect configuration errors at instantiation time instead of at mapping time.
 *
 * <p>
 * This class only collects the facts about a transformation. The verdict is made by {@link NullnessRules}, so that a
 * static analysis evaluating the same facts produces identical results.
 * </p>
 *
 * @see NullnessPolicy
 */
class NullnessValidator {

  private static final Logger LOGGER = Logger.getLogger(NullnessValidator.class.getName());

  private final MappingConfiguration<?, ?> mapping;

  private final List<String> violations = new LinkedList<>();

  /**
   * A freshly created destination object used to determine whether a destination property holds a non-null value
   * although it was not written by the mapping. The probe is created on demand, because creating it is only necessary
   * if a violation is suspected at all.
   */
  private @Nullable Object destinationProbe;

  private boolean destinationProbeResolved;

  private NullnessValidator(MappingConfiguration<?, ?> mapping) {
    this.mapping = mapping;
  }

  /**
   * Validates the specified transformations against the nullness declared on the mapped properties and applies the
   * specified policy on the detected violations.
   *
   * @param mapping The mapping configuration that is currently built.
   * @param transformations The transformations of the mapping configuration.
   * @param policy The policy to apply on detected violations.
   * @throws MappingException Thrown if violations were detected and the policy is {@link NullnessPolicy#ERROR}.
   */
  static void validate(MappingConfiguration<?, ?> mapping, Set<Transformation> transformations, NullnessPolicy policy) {
    if (policy == NullnessPolicy.OFF) {
      return;
    }
    NullnessValidator validator = new NullnessValidator(mapping);
    for (Transformation transformation : transformations) {
      validator.validateTransformation(transformation);
    }
    validator.report(policy);
  }

  private void validateTransformation(Transformation transformation) {
    if (transformation instanceof ReassignTransformation) {
      // Covers the reassign operation as well as the implicit mappings, which use MapTransformation.
      validateReassign((ReassignTransformation) transformation);
    } else if (transformation instanceof ReplaceCollectionTransformation) {
      // The collection variants skip the mapping if the whole source collection is null. Skipping null items is a
      // different configuration that does not affect the destination property itself.
      validateSkipped(transformation);
    } else if (transformation instanceof ReplaceTransformation) {
      if (((ReplaceTransformation<?, ?>) transformation).isSkipWhenNull()) {
        validateSkipped(transformation);
      }
      // Without skip-when-null the transformation function is called with the null value and its result is written to
      // the destination. Whether the function accepts and returns null is part of its signature and therefore checked
      // by the compiler, not here.
    } else if (transformation instanceof PropertyPathTransformation
        || transformation instanceof PropertyPathCollectionTransformation) {
      // Property paths are null-friendly: the mapping is skipped if the source property is null. Whether the path
      // itself evaluates to null cannot be determined reflectively.
      validateSkipped(transformation);
    }
    // All remaining transformations either do not write a destination property (omit) or produce their value from a
    // function or supplier whose nullness is not available reflectively (set, restructure).
  }

  private void validateReassign(ReassignTransformation transformation) {
    PropertyDescriptor sourceProperty = transformation.getSourceProperty();
    PropertyDescriptor destinationProperty = transformation.getDestinationProperty();
    if (sourceProperty == null || destinationProperty == null) {
      return;
    }
    Nullness source = sourceNullness(sourceProperty);
    Nullness destination = destinationNullness(destinationProperty);
    Class<?> destinationType = destinationProperty.getPropertyType();

    add(transformation, NullnessRules.reassign(source, destination, destinationType.isPrimitive(),
        mapping.isWriteNull(), hasNonNullDefault(destinationProperty)));

    validateTypeArguments(transformation, sourceProperty, destinationProperty);
  }

  /**
   * Validates the nullness of the element type of a mapped collection and of the key and value types of a mapped map.
   * Only the first level of the generic type is inspected, nested collections and maps are not evaluated.
   */
  private void validateTypeArguments(ReassignTransformation transformation, PropertyDescriptor sourceProperty,
      PropertyDescriptor destinationProperty) {
    Method sourceReadMethod = sourceProperty.getReadMethod();
    Method destinationReadMethod = destinationProperty.getReadMethod();
    if (sourceReadMethod == null || destinationReadMethod == null) {
      return;
    }
    Class<?> sourceType = sourceProperty.getPropertyType();
    if (isCollection(sourceType)) {
      add(transformation, NullnessRules.collectionElement(JSpecifyNullness.ofTypeArgument(sourceReadMethod, 0)));
    } else if (Map.class.isAssignableFrom(sourceType)) {
      for (int index = 0; index < 2; index++) {
        Nullness entry = JSpecifyNullness.ofTypeArgument(sourceReadMethod, index);
        boolean mapperRequired = requiresMapper(sourceReadMethod, destinationReadMethod, index);
        add(transformation, NullnessRules.mapEntry(entry, mapperRequired));
      }
    }
  }

  /**
   * Determines whether the specified type argument is converted by a registered mapper instead of being mapped by
   * reference. Equal types are always mapped by reference, so null values are passed through.
   */
  private boolean requiresMapper(Method sourceReadMethod, Method destinationReadMethod, int index) {
    Class<?> sourceArgument = JSpecifyNullness.typeArgumentOf(sourceReadMethod, index);
    Class<?> destinationArgument = JSpecifyNullness.typeArgumentOf(destinationReadMethod, index);
    if (sourceArgument == null || destinationArgument == null) {
      return false;
    }
    return !sourceArgument.equals(destinationArgument);
  }

  private void validateSkipped(Transformation transformation) {
    PropertyDescriptor sourceProperty = transformation.getSourceProperty();
    PropertyDescriptor destinationProperty = transformation.getDestinationProperty();
    if (sourceProperty == null || destinationProperty == null) {
      return;
    }
    Nullness source = sourceNullness(sourceProperty);
    Nullness destination = destinationNullness(destinationProperty);
    add(transformation, NullnessRules.skipWhenNull(source, destination, hasNonNullDefault(destinationProperty)));
  }

  private Nullness sourceNullness(PropertyDescriptor sourceProperty) {
    Method readMethod = sourceProperty.getReadMethod();
    if (readMethod == null) {
      return Nullness.UNSPECIFIED;
    }
    return JSpecifyNullness.ofReturnType(readMethod);
  }

  /**
   * Returns the nullness of a destination property. The value is written through the setter, so the nullness is taken
   * from its parameter. If the destination property has no setter, the getter is used as a fallback.
   */
  private Nullness destinationNullness(PropertyDescriptor destinationProperty) {
    Method writeMethod = destinationProperty.getWriteMethod();
    if (writeMethod != null) {
      return JSpecifyNullness.ofParameter(writeMethod);
    }
    Method readMethod = destinationProperty.getReadMethod();
    if (readMethod == null) {
      return Nullness.UNSPECIFIED;
    }
    return JSpecifyNullness.ofReturnType(readMethod);
  }

  /**
   * Determines whether the specified destination property holds a non-null value in a freshly created destination
   * object. In that case a skipped mapping does not leave a null value behind and the mapping is sound.
   */
  private boolean hasNonNullDefault(PropertyDescriptor destinationProperty) {
    Object probe = destinationProbe();
    Method readMethod = destinationProperty.getReadMethod();
    if (probe == null || readMethod == null) {
      return false;
    }
    try {
      readMethod.setAccessible(true);
      return readMethod.invoke(probe) != null;
    } catch (Exception e) {
      // The default value cannot be determined. Assume that there is none, which is the conservative assumption for
      // the nullness rules.
      return false;
    }
  }

  private @Nullable Object destinationProbe() {
    if (!destinationProbeResolved) {
      destinationProbeResolved = true;
      try {
        destinationProbe = mapping.createDestination();
      } catch (Exception e) {
        // Creating the destination object may fail for types that do not tolerate being instantiated without
        // arguments. The nullness validation must not break the mapper for that reason.
        destinationProbe = null;
      }
    }
    return destinationProbe;
  }

  private void add(Transformation transformation, Optional<NullnessRule> rule) {
    rule.ifPresent(violated -> violations.add(String.format("- %s%n  %s%n  Hint: %s", transformation.toString(true),
        violated.getDescription(), violated.getHint())));
  }

  private void report(NullnessPolicy policy) {
    if (violations.isEmpty()) {
      return;
    }
    String message = MappingException.nullnessViolationMessage(mapping.getSource(), mapping.getDestination(),
        violations);
    if (policy == NullnessPolicy.ERROR) {
      throw new MappingException(message);
    }
    LOGGER.log(Level.WARNING, message);
  }

}
