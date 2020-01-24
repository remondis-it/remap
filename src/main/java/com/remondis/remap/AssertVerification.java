package com.remondis.remap;


/**
 * Interface to implement custom verification tasks evaluated by {@link AssertConfiguration#ensure()}. Use this method
 * for verifications of mapping operations that cannot be performed using {@link Transformation#equals(Object)}.
 */
@FunctionalInterface
public interface AssertVerification {
  /**
   * @throws AssertionError Throws an {@link AssertionError} if the verification failed. Please use user-friendly text
   *         messages that explain the expected and actual state.
   */
  public void verify() throws AssertionError;
}
