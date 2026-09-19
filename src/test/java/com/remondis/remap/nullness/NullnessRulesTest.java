package com.remondis.remap.nullness;

import static com.remondis.remap.Nullness.NON_NULL;
import static com.remondis.remap.Nullness.NULLABLE;
import static com.remondis.remap.Nullness.UNSPECIFIED;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Nullness;
import com.remondis.remap.NullnessPolicy;
import com.remondis.remap.NullnessRule;
import com.remondis.remap.NullnessRules;

/**
 * Tests the nullness rules in isolation. These rules are the contract that a static analysis has to implement
 * identically, so they are specified independently of the reflective evaluation.
 */
public class NullnessRulesTest {

  @Test
  public void reassignShouldReportUnwrittenDestination() {
    assertThat(reassign(NULLABLE, NON_NULL)).contains(NullnessRule.UNWRITTEN_DESTINATION);
  }

  @Test
  public void reassignShouldAcceptSoundCombinations() {
    assertThat(reassign(NULLABLE, NULLABLE)).isEmpty();
    assertThat(reassign(NULLABLE, UNSPECIFIED)).isEmpty();
    assertThat(reassign(NON_NULL, NON_NULL)).isEmpty();
    assertThat(reassign(UNSPECIFIED, NON_NULL)).isEmpty();
  }

  @Test
  public void reassignShouldAcceptNonNullDefaultInDestination() {
    assertThat(NullnessRules.reassign(NULLABLE, NON_NULL, false, false, true)).isEmpty();
  }

  @Test
  public void reassignShouldIgnorePrimitiveDestinationIfMappingIsSkipped() {
    assertThat(NullnessRules.reassign(NULLABLE, NON_NULL, true, false, false)).isEmpty();
  }

  @Test
  public void reassignShouldReportNullWrittenToPrimitive() {
    assertThat(NullnessRules.reassign(NULLABLE, NON_NULL, true, true, false))
        .contains(NullnessRule.NULL_WRITTEN_TO_PRIMITIVE);
  }

  @Test
  public void reassignShouldReportNullWrittenToNonNull() {
    assertThat(NullnessRules.reassign(NULLABLE, NON_NULL, false, true, false))
        .contains(NullnessRule.NULL_WRITTEN_TO_NON_NULL);
  }

  /**
   * A non-null default value does not help if the mapper explicitly writes null into the destination property.
   */
  @Test
  public void reassignShouldReportNullWrittenToNonNullDespiteDefaultValue() {
    assertThat(NullnessRules.reassign(NULLABLE, NON_NULL, false, true, true))
        .contains(NullnessRule.NULL_WRITTEN_TO_NON_NULL);
  }

  @Test
  public void skipWhenNullShouldReportUnwrittenDestination() {
    assertThat(NullnessRules.skipWhenNull(NULLABLE, NON_NULL, false)).contains(NullnessRule.UNWRITTEN_DESTINATION);
    assertThat(NullnessRules.skipWhenNull(NULLABLE, NON_NULL, true)).isEmpty();
    assertThat(NullnessRules.skipWhenNull(NULLABLE, NULLABLE, false)).isEmpty();
    assertThat(NullnessRules.skipWhenNull(NON_NULL, NON_NULL, false)).isEmpty();
  }

  @Test
  public void collectionElementShouldReportNullableElements() {
    assertThat(NullnessRules.collectionElement(NULLABLE)).contains(NullnessRule.NULLABLE_COLLECTION_ELEMENT);
    assertThat(NullnessRules.collectionElement(NON_NULL)).isEmpty();
    assertThat(NullnessRules.collectionElement(UNSPECIFIED)).isEmpty();
  }

  /**
   * Null keys and values are only denied if they are converted by a registered mapper. Entries mapped by reference
   * pass null through.
   */
  @Test
  public void mapEntryShouldOnlyReportEntriesConvertedByMapper() {
    assertThat(NullnessRules.mapEntry(NULLABLE, true)).contains(NullnessRule.NULLABLE_MAP_ENTRY);
    assertThat(NullnessRules.mapEntry(NULLABLE, false)).isEmpty();
    assertThat(NullnessRules.mapEntry(NON_NULL, true)).isEmpty();
  }

  @Test
  public void defaultPolicyShouldBeOff() {
    assertThat(NullnessPolicy.defaultPolicy()).isEqualTo(NullnessPolicy.OFF);
  }

  @Test
  public void defaultPolicyShouldBeReadFromSystemProperty() {
    System.setProperty(NullnessPolicy.SYSTEM_PROPERTY, "error");
    try {
      assertThat(NullnessPolicy.defaultPolicy()).isEqualTo(NullnessPolicy.ERROR);
    } finally {
      System.clearProperty(NullnessPolicy.SYSTEM_PROPERTY);
    }
  }

  private static Optional<NullnessRule> reassign(Nullness source, Nullness destination) {
    return NullnessRules.reassign(source, destination, false, false, false);
  }

}
