package com.remondis.remap;

/**
 * This is the ReMap API entrypoint to create instances of {@link Mapper}.
 *
 * @author schuettec
 *
 */
public class AssertMapping {

  /**
   * Creates a new specification of assertions that are to be checked for the specified mapper instance.
   *
   * @param mapper
   *        The {@link Mapper} instance.
   * @return Returns a new {@link AssertConfiguration} for method changing.
   */
  public static <S, D> AssertConfiguration<S, D> of(Mapper<S, D> mapper) {
    return new AssertConfiguration<S, D>(mapper);
  }

}
