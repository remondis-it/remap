package com.remondis.remap;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;
import java.util.List;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

/**
 * Tests the reflective evaluation of the JSpecify annotations, especially the resolution of the null-marked scopes.
 */
public class JSpecifyNullnessTest {

  @NullMarked
  static class MarkedBean {

    public String getNonNull() {
      return "";
    }

    public @Nullable String getNullable() {
      return null;
    }

    public int getPrimitive() {
      return 0;
    }

    public List<@Nullable String> getNullableElements() {
      return List.of();
    }

    public List<String> getNonNullElements() {
      return List.of();
    }

    public void setNonNull(String value) {
    }

    public void setNullable(@Nullable String value) {
    }

    @NullUnmarked
    public String getUnmarkedByMethod() {
      return "";
    }

  }

  @NullUnmarked
  static class UnmarkedBean {

    public String getUnspecified() {
      return "";
    }

    public @Nullable String getNullable() {
      return null;
    }

  }

  @Test
  public void shouldReadNullnessOfReturnTypes() throws Exception {
    assertThat(JSpecifyNullness.ofReturnType(method(MarkedBean.class, "getNonNull"))).isEqualTo(Nullness.NON_NULL);
    assertThat(JSpecifyNullness.ofReturnType(method(MarkedBean.class, "getNullable"))).isEqualTo(Nullness.NULLABLE);
    assertThat(JSpecifyNullness.ofReturnType(method(MarkedBean.class, "getPrimitive"))).isEqualTo(Nullness.NON_NULL);
  }

  @Test
  public void shouldReadNullnessOfSetterParameters() throws Exception {
    assertThat(JSpecifyNullness.ofParameter(method(MarkedBean.class, "setNonNull", String.class)))
        .isEqualTo(Nullness.NON_NULL);
    assertThat(JSpecifyNullness.ofParameter(method(MarkedBean.class, "setNullable", String.class)))
        .isEqualTo(Nullness.NULLABLE);
  }

  @Test
  public void shouldReadNullnessOfTypeArguments() throws Exception {
    assertThat(JSpecifyNullness.ofTypeArgument(method(MarkedBean.class, "getNullableElements"), 0))
        .isEqualTo(Nullness.NULLABLE);
    assertThat(JSpecifyNullness.ofTypeArgument(method(MarkedBean.class, "getNonNullElements"), 0))
        .isEqualTo(Nullness.NON_NULL);
    // The return type is not parameterized.
    assertThat(JSpecifyNullness.ofTypeArgument(method(MarkedBean.class, "getNonNull"), 0))
        .isEqualTo(Nullness.UNSPECIFIED);
  }

  @Test
  public void shouldResolveRawTypeOfTypeArguments() throws Exception {
    assertThat(JSpecifyNullness.typeArgumentOf(method(MarkedBean.class, "getNullableElements"), 0))
        .isEqualTo(String.class);
    assertThat(JSpecifyNullness.typeArgumentOf(method(MarkedBean.class, "getNonNull"), 0)).isNull();
  }

  /**
   * An unannotated type usage outside a null-marked scope has unspecified nullness and must never be treated as
   * non-null.
   */
  @Test
  public void shouldTreatUnmarkedScopesAsUnspecified() throws Exception {
    assertThat(JSpecifyNullness.ofReturnType(method(UnmarkedBean.class, "getUnspecified")))
        .isEqualTo(Nullness.UNSPECIFIED);
    assertThat(JSpecifyNullness.ofReturnType(method(MarkedBean.class, "getUnmarkedByMethod")))
        .isEqualTo(Nullness.UNSPECIFIED);
  }

  /**
   * An explicit @Nullable annotation is meaningful even outside a null-marked scope.
   */
  @Test
  public void shouldReadExplicitNullableInUnmarkedScope() throws Exception {
    assertThat(JSpecifyNullness.ofReturnType(method(UnmarkedBean.class, "getNullable"))).isEqualTo(Nullness.NULLABLE);
  }

  private static Method method(Class<?> type, String name, Class<?>... parameterTypes) throws NoSuchMethodException {
    return type.getMethod(name, parameterTypes);
  }

}
