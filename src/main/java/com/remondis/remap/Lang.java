package com.remondis.remap;

/**
 * A utility class to provide some language features.
 *
 * @author schuettec
 */
class Lang {

  /**
   * This method throws an {@link IllegalArgumentException} if the specified argument is null.
   *
   * @param fieldName The parameter name.
   * @param argument The actual argument.
   * @return Returns the argument
   * @throws IllegalArgumentException Thrown with a detailed message if argument is <code>null</code>. Returns
   *         immediately otherwise.
   */
  static <T> T denyNull(String fieldName, T argument) throws IllegalArgumentException {
    if (argument == null) {
      if (fieldName == null) {
        throw new IllegalArgumentException("Argument must not be null.");
      } else {
        throw new IllegalArgumentException(String.format("Argument %s must not be null.", fieldName));
      }
    }
    return argument;
  }

}
