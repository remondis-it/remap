package com.remondis.remap;

/**
 * This class defines the lambda that receives an object of either a source or destination a mapping type. The lambda is
 * implemented with calling the get-method to select the corresponding property. The mapping framework then detects the
 * get-method invocation on the destination object and selects the corresponding property for further mapping
 * configuration. The mapping configuration is specified with the surrounding configuration method like {@link
 * MappingConfiguration#omitInDestination(FieldSelector)},{@link MappingConfiguration#omitInSource(FieldSelector)} .
 *
 * @param <T> The object type selecting a field on.
 * @param <R> The type of the field.
 * @author schuettec
 */
@FunctionalInterface
public interface TypedSelector<R, T> {

  /**
   * This method is used to perform a get-method invocation of the specified destination object and returning its value.
   * This invocation tells the mapper which property is to be selected for the following configuration and what type it
   * has.
   *
   * @param destination The destination object to perform a get-method invocation on.
   * @return Returns the return value of the performed get-method call.
   */
  R selectField(T destination);

}
