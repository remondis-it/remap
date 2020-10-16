package com.remondis.remap;

/**
 * This class defines the lambda that receives an object of either a source or destination type. The lambda is
 * implemented with calling the get-method to select the corresponding property. The mapping framework then detects the
 * get-method invocation on the destination object and selects the corresponding property for further mapping
 * configuration. The mapping configuration is specified with the surrounding configuration method like {@link
 * MappingConfiguration#omitInDestination(FieldSelector)},{@link MappingConfiguration#omitInSource(FieldSelector)} .
 *
 * @param <T> The object type selecting a field on.
 * @author schuettec
 */
@FunctionalInterface
public interface FieldSelector<T> {

  /**
   * This method is used to perform a get-method invocation of the specified destination object. This invocation tells
   * the mapper which property is to be selected for the following configuration.
   *
   * @param destination The destination object to perform a get-method invocation on.
   */
  void selectField(T destination);

}
