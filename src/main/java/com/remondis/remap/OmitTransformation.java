package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;
import static com.remondis.remap.Properties.asString;

import java.beans.PropertyDescriptor;

/**
 * The omit transformation marks a property as omitted.
 *
 * @author schuettec
 */
class OmitTransformation extends Transformation {

  private static final String OMITTING_MSG = "Omitting %s";

  private OmitTransformation(Mapping<?, ?> mapping, PropertyDescriptor sourceProperty,
      PropertyDescriptor destinationProperty) {
    super(mapping, sourceProperty, destinationProperty);
  }

  /**
   * Omits a property in the destination object.
   *
   * @param mapping The mapper to link to.
   * @param destinationProperty the property to omit in the destination
   * @return Returns a new {@link OmitTransformation}.
   */
  static OmitTransformation omitDestination(Mapping<?, ?> mapping, PropertyDescriptor destinationProperty) {
    denyNull("mapping", mapping);
    denyNull("destinationProperty", destinationProperty);
    return new OmitTransformation(mapping, null, destinationProperty);
  }

  /**
   * Omits a property in the source object.
   *
   * @param mapping The mapper to link to.
   * @param sourceProperty the property to omit in the source
   * @return Returns a new {@link OmitTransformation}.
   */
  static OmitTransformation omitSource(Mapping<?, ?> mapping, PropertyDescriptor sourceProperty) {
    denyNull("mapping", mapping);
    denyNull("destinationProperty", sourceProperty);
    return new OmitTransformation(mapping, sourceProperty, null);
  }

  @Override
  protected void performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty, Object destination) throws MappingException {
  }

  @Override
  public String toString() {
    if (this.sourceProperty != null) {
      return String.format(OMITTING_MSG, asString(sourceProperty));
    } else {
      return String.format(OMITTING_MSG, asString(destinationProperty));
    }
  }

  @Override
  protected void validateTransformation() throws MappingException {
  }

}
