package com.remondis.remap.nullness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;
import com.remondis.remap.NullnessPolicy;
import com.remondis.remap.NullnessRule;
import com.remondis.remap.nullness.unmarked.UnmarkedTextBean;

/**
 * Tests the nullness validation that is performed while a mapper is built.
 */
public class NullnessValidationTest {

  @Test
  public void shouldDenyNullableSourceMappedToNonNullDestination() {
    assertThatThrownBy(() -> Mapping.from(NullableTextBean.class)
        .to(NonNullTextBean.class)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper()).isInstanceOf(MappingException.class)
        .hasMessageContaining(NullnessRule.UNWRITTEN_DESTINATION.getDescription())
        .hasMessageContaining("text");
  }

  @Test
  public void shouldAllowNullableSourceMappedToNullableDestination() {
    assertThatCode(() -> Mapping.from(NullableTextBean.class)
        .to(NullableTextBean.class)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper()).doesNotThrowAnyException();
  }

  @Test
  public void shouldAllowNonNullSourceMappedToNonNullDestination() {
    assertThatCode(() -> Mapping.from(NonNullTextBean.class)
        .to(NonNullTextBean.class)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper()).doesNotThrowAnyException();
  }

  /**
   * The destination property holds a non-null default value, so skipping the mapping does not leave a null value
   * behind. The probe instance created by the validation detects this.
   */
  @Test
  public void shouldAllowNullableSourceIfDestinationHasNonNullDefault() {
    assertThatCode(() -> Mapping.from(NullableTextBean.class)
        .to(DefaultedTextBean.class)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper()).doesNotThrowAnyException();
  }

  /**
   * Properties that are not covered by a null-marked scope have unspecified nullness and never produce a violation.
   */
  @Test
  public void shouldIgnorePropertiesWithUnspecifiedNullness() {
    assertThatCode(() -> Mapping.from(UnmarkedTextBean.class)
        .to(NonNullTextBean.class)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper()).doesNotThrowAnyException();

    assertThatCode(() -> Mapping.from(NullableTextBean.class)
        .to(UnmarkedTextBean.class)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper()).doesNotThrowAnyException();
  }

  @SuppressWarnings("deprecation")
  @Test
  public void shouldDenyWritingNullToPrimitiveDestination() {
    assertThatThrownBy(() -> Mapping.from(NullableNumberBean.class)
        .to(PrimitiveNumberBean.class)
        .writeNullIfSourceIsNull()
        .validateNullness(NullnessPolicy.ERROR)
        .mapper()).isInstanceOf(MappingException.class)
        .hasMessageContaining(NullnessRule.NULL_WRITTEN_TO_PRIMITIVE.getDescription());
  }

  /**
   * Without writeNullIfSourceIsNull the mapping is skipped, so the primitive destination keeps its primitive default
   * value. That is a loss of information, but not a nullness violation.
   */
  @Test
  public void shouldAllowSkippedMappingToPrimitiveDestination() {
    assertThatCode(() -> Mapping.from(NullableNumberBean.class)
        .to(PrimitiveNumberBean.class)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper()).doesNotThrowAnyException();
  }

  @SuppressWarnings("deprecation")
  @Test
  public void shouldDenyWritingNullToNonNullDestination() {
    assertThatThrownBy(() -> Mapping.from(NullableTextBean.class)
        .to(NonNullTextBean.class)
        .writeNullIfSourceIsNull()
        .validateNullness(NullnessPolicy.ERROR)
        .mapper()).isInstanceOf(MappingException.class)
        .hasMessageContaining(NullnessRule.NULL_WRITTEN_TO_NON_NULL.getDescription());
  }

  @Test
  public void shouldDenyNullableCollectionElements() {
    assertThatThrownBy(() -> Mapping.from(NullableElementsBean.class)
        .to(NonNullElementsBean.class)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper()).isInstanceOf(MappingException.class)
        .hasMessageContaining(NullnessRule.NULLABLE_COLLECTION_ELEMENT.getDescription());
  }

  @Test
  public void shouldAllowNonNullCollectionElements() {
    assertThatCode(() -> Mapping.from(NonNullElementsBean.class)
        .to(NonNullElementsBean.class)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper()).doesNotThrowAnyException();
  }

  @Test
  public void shouldDenyNullableMapValuesConvertedByMapper() {
    Mapper<Address, AddressDto> addressMapper = Mapping.from(Address.class)
        .to(AddressDto.class)
        .mapper();

    assertThatThrownBy(() -> Mapping.from(NullableMapValuesBean.class)
        .to(MappedMapValuesBean.class)
        .useMapper(addressMapper)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper()).isInstanceOf(MappingException.class)
        .hasMessageContaining(NullnessRule.NULLABLE_MAP_ENTRY.getDescription());
  }

  /**
   * The replace operation with skip-when-null does not write the destination property if the source value is null.
   */
  @Test
  public void shouldDenySkipWhenNullMappedToNonNullDestination() {
    assertThatThrownBy(() -> Mapping.from(NullableTextBean.class)
        .to(NonNullTextBean.class)
        .replace(NullableTextBean::getText, NonNullTextBean::getText)
        .withSkipWhenNull(String::trim)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper()).isInstanceOf(MappingException.class)
        .hasMessageContaining(NullnessRule.UNWRITTEN_DESTINATION.getDescription());
  }

  /**
   * The replace operation without skip-when-null always writes the destination property. Whether the transformation
   * function accepts and returns null is declared by its signature and therefore checked by the compiler.
   */
  @Test
  public void shouldNotReportReplaceWithoutSkipWhenNull() {
    assertThatCode(() -> Mapping.from(NullableTextBean.class)
        .to(NonNullTextBean.class)
        .replace(NullableTextBean::getText, NonNullTextBean::getText)
        .with(text -> text == null ? "" : text)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper()).doesNotThrowAnyException();
  }

  @Test
  public void shouldBeDisabledByDefault() {
    assertThatCode(() -> Mapping.from(NullableTextBean.class)
        .to(NonNullTextBean.class)
        .mapper()).doesNotThrowAnyException();
  }

  @Test
  public void shouldLogViolationsWithWarnPolicy() {
    Logger logger = Logger.getLogger("com.remondis.remap.NullnessValidator");
    List<LogRecord> records = new ArrayList<>();
    Handler handler = new Handler() {
      @Override
      public void publish(LogRecord record) {
        records.add(record);
      }

      @Override
      public void flush() {
      }

      @Override
      public void close() {
      }
    };
    logger.addHandler(handler);
    try {
      Mapper<NullableTextBean, NonNullTextBean> mapper = Mapping.from(NullableTextBean.class)
          .to(NonNullTextBean.class)
          .validateNullness(NullnessPolicy.WARN)
          .mapper();

      assertThat(mapper).isNotNull();
      assertThat(records).hasSize(1);
      assertThat(records.get(0)
          .getLevel()).isEqualTo(Level.WARNING);
      assertThat(records.get(0)
          .getMessage()).contains(NullnessRule.UNWRITTEN_DESTINATION.getDescription());
    } finally {
      logger.removeHandler(handler);
    }
  }

  /**
   * The mapping itself must not change its behaviour because of the validation.
   */
  @Test
  public void shouldNotAffectTheMapping() {
    Mapper<NullableTextBean, DefaultedTextBean> mapper = Mapping.from(NullableTextBean.class)
        .to(DefaultedTextBean.class)
        .validateNullness(NullnessPolicy.ERROR)
        .mapper();

    assertThat(mapper.map(new NullableTextBean("value"))
        .getText()).isEqualTo("value");
    // The mapping is skipped for a null source value, so the default value of the destination is kept.
    assertThat(mapper.map(new NullableTextBean(null))
        .getText()).isEqualTo("default");
  }

}
