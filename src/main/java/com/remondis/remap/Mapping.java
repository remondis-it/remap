package com.remondis.remap;

/**
 * This class provides the starting point to create a new type mapping. The mapped types must follow the Java Bean
 * Convention.
 * <ul>
 * <li>A property is a field with any visibility</li>
 * <li>A property has a
 * public getter/setter pair exactly named as the field</li>
 * <li>Boolean values have is/setter
 * methods.</li>
 * <li>A bean has a default zero-args constructor.</li>
 * </ul>
 *
 */
public final class Mapping {
  /**
   * Specifies the source data type to map from.
   *
   * @param source
   *        the data source type.
   * @return Returns a {@link Types} object for further mapping configuration.
   */
  public static <S> Types<S> from(Class<S> source) {
    return new Types<>(source);
  }
}
