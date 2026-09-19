package com.remondis.remap.nullness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;

/**
 * Proves that every nullness rule describes a mapping behaviour that really happens. The nullness validation claims
 * that the detected situations are bugs - these tests execute the mappings with the validation disabled and assert the
 * broken outcome the rules warn about.
 *
 * <p>
 * Without these tests the validation would report situations whose consequences are only assumed. If a mapping
 * operation ever changes its null handling, the test of the corresponding rule fails and the rule has to be adjusted.
 * </p>
 */
public class NullnessRuleBehaviourTest {

  /**
   * UNWRITTEN_DESTINATION: the destination property is declared non-null, but the skipped mapping leaves it null.
   */
  @Test
  public void reassignShouldLeaveNullInNonNullDestination() {
    Mapper<NullableTextBean, NonNullTextBean> mapper = Mapping.from(NullableTextBean.class)
        .to(NonNullTextBean.class)
        .mapper();

    NonNullTextBean destination = mapper.map(new NullableTextBean(null));

    // The property is declared non-null, but holds null after the mapping.
    assertThat(destination.getText()).isNull();
  }

  /**
   * UNWRITTEN_DESTINATION for the skip-when-null variant of the replace operation.
   */
  @Test
  public void skipWhenNullShouldLeaveNullInNonNullDestination() {
    Mapper<NullableTextBean, NonNullTextBean> mapper = Mapping.from(NullableTextBean.class)
        .to(NonNullTextBean.class)
        .replace(NullableTextBean::getText, NonNullTextBean::getText)
        .withSkipWhenNull(String::trim)
        .mapper();

    NonNullTextBean destination = mapper.map(new NullableTextBean(null));

    assertThat(destination.getText()).isNull();
  }

  /**
   * NULL_WRITTEN_TO_NON_NULL: with writeNullIfSourceIsNull the null value is actively written into the destination.
   */
  @SuppressWarnings("deprecation")
  @Test
  public void writeNullShouldWriteNullIntoNonNullDestination() {
    Mapper<NullableTextBean, NonNullTextBean> mapper = Mapping.from(NullableTextBean.class)
        .to(NonNullTextBean.class)
        .writeNullIfSourceIsNull()
        .mapper();

    NonNullTextBean destination = mapper.map(new NullableTextBean(null));

    assertThat(destination.getText()).isNull();
  }

  /**
   * NULL_WRITTEN_TO_PRIMITIVE: writing null into a primitive setter fails while mapping.
   */
  @SuppressWarnings("deprecation")
  @Test
  public void writeNullToPrimitiveShouldFailWhileMapping() {
    Mapper<NullableNumberBean, PrimitiveNumberBean> mapper = Mapping.from(NullableNumberBean.class)
        .to(PrimitiveNumberBean.class)
        .writeNullIfSourceIsNull()
        .mapper();

    // The concrete root cause of the failed setter invocation is a JDK implementation detail - it is an
    // IllegalArgumentException on older JDKs and a NullPointerException from unboxing on current ones. What the
    // caller sees is a MappingException naming the property, so that is what the rule describes.
    assertThatThrownBy(() -> mapper.map(new NullableNumberBean())).isInstanceOf(MappingException.class)
        .hasMessageContaining("number");
  }

  /**
   * Without writeNullIfSourceIsNull the primitive destination keeps its primitive default value. This is the reason
   * why the rules do not report this combination.
   */
  @Test
  public void skippedMappingShouldKeepPrimitiveDefault() {
    Mapper<NullableNumberBean, PrimitiveNumberBean> mapper = Mapping.from(NullableNumberBean.class)
        .to(PrimitiveNumberBean.class)
        .mapper();

    assertThat(mapper.map(new NullableNumberBean())
        .getNumber()).isZero();
  }

  /**
   * NULLABLE_COLLECTION_ELEMENT: ReMap denies null elements while mapping collections.
   */
  @Test
  public void nullCollectionElementShouldFailWhileMapping() {
    Mapper<NullableElementsBean, NonNullElementsBean> mapper = Mapping.from(NullableElementsBean.class)
        .to(NonNullElementsBean.class)
        .mapper();

    NullableElementsBean source = new NullableElementsBean();
    source.setTexts(Arrays.asList("a", null));

    assertThatThrownBy(() -> mapper.map(source)).isInstanceOf(MappingException.class);
  }

  /**
   * NULLABLE_MAP_ENTRY: a map value converted by a registered mapper must not be null, because a mapper denies null
   * input.
   */
  @Test
  public void nullMapValueShouldFailWhileMapping() {
    Mapper<Address, AddressDto> addressMapper = Mapping.from(Address.class)
        .to(AddressDto.class)
        .mapper();
    Mapper<NullableMapValuesBean, MappedMapValuesBean> mapper = Mapping.from(NullableMapValuesBean.class)
        .to(MappedMapValuesBean.class)
        .useMapper(addressMapper)
        .mapper();

    Map<String, Address> addresses = new HashMap<>();
    addresses.put("home", null);
    NullableMapValuesBean source = new NullableMapValuesBean();
    source.setAddresses(addresses);

    assertThatThrownBy(() -> mapper.map(source)).isInstanceOf(MappingException.class);
  }

}
