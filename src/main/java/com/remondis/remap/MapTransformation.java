package com.remondis.remap;

import com.googlecode.openbeans.PropertyDescriptor;

import static com.remondis.remap.Properties.asString;


/**
 * The map transformation is the simplest mapping operation. It just reassigns a field from the source object to a
 * destination object while the property name AND [ types are equal OR a matching mapper is
 * registered on the parent mapper ]. Therefore the mapping transformation is
 * a special case of {@link ReassignTransformation}. This mapping transformation is only used
 * internally to distinguish from mappings configured by the user and implicit field mappings created by the mapper
 * itself.
 */
public class MapTransformation extends ReassignTransformation {

  private static final String MAP_MSG = "Map %s\n   to %s";

  MapTransformation(MappingConfiguration<?, ?> mapping, PropertyDescriptor sourceProperty,
      PropertyDescriptor destinationProperty) {
    super(mapping, sourceProperty, destinationProperty);
    denyReassign(sourceProperty, destinationProperty);
  }

  private void denyReassign(PropertyDescriptor sourceProperty, PropertyDescriptor destinationProperty) {
    if (!sourceProperty.getName()
        .equals(destinationProperty.getName())) {
      throw new MappingException("Attempt to perform a reassign with MapTransformation - implementation fault!");
    }
  }

  @Override
  public String toString() {
    return String.format(MAP_MSG, asString(sourceProperty), asString(destinationProperty));
  }

}
