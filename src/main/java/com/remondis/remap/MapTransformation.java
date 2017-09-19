package com.remondis.remap;

import static com.remondis.remap.Properties.asString;

import java.beans.PropertyDescriptor;

public class MapTransformation extends ReassignTransformation {

  private static final String MAP_MSG = "Map %s\n   to %s";

  MapTransformation(Mapping<?, ?> mapping, PropertyDescriptor sourceProperty, PropertyDescriptor destinationProperty) {
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
