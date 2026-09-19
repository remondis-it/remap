package com.remondis.remap;

/**
 * Defines how a {@link MappingConfiguration} reacts on nullness violations detected while building the mapper.
 *
 * @see MappingConfiguration#validateNullness(NullnessPolicy)
 */
public enum NullnessPolicy {

  /**
   * Nullness violations are not detected at all. This is the default to stay backwards compatible with mapping
   * configurations built before the nullness validation was introduced.
   */
  OFF,

  /**
   * Nullness violations are reported to {@link java.util.logging.Logger} at
   * {@link java.util.logging.Level#WARNING}. Use this policy to introduce the nullness validation in an existing code
   * base without breaking the build.
   */
  WARN,

  /**
   * Nullness violations abort the creation of the mapper with a {@link MappingException}.
   */
  ERROR;

  /**
   * The name of the system property that defines the default policy for all mappers that do not configure a policy
   * themselves. The value is the name of one of the constants of this enumeration, ignoring the case.
   */
  public static final String SYSTEM_PROPERTY = "remap.nullness.policy";

  /**
   * Returns the default policy that is applied to mappers that do not specify a policy using
   * {@link MappingConfiguration#validateNullness(NullnessPolicy)}. The default policy is {@link #OFF} unless the
   * system property {@value #SYSTEM_PROPERTY} specifies a different policy.
   *
   * @return Returns the default {@link NullnessPolicy}.
   */
  public static NullnessPolicy defaultPolicy() {
    String configured = System.getProperty(SYSTEM_PROPERTY);
    if (configured == null) {
      return OFF;
    }
    try {
      return valueOf(configured.trim()
          .toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new MappingException(String.format(
          "The system property '%s' specifies the unknown nullness policy '%s'. Supported values are: OFF, WARN, ERROR.",
          SYSTEM_PROPERTY, configured));
    }
  }

}
