package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;
import static com.remondis.remap.Properties.asString;
import static java.util.Objects.nonNull;

import java.beans.PropertyDescriptor;

/**
 * The omit transformation marks a property as omitted.
 *
 * @author schuettec
 */
class OmitTransformation extends Transformation {

  private static final String OMITTING_MSG = "Omitting %s";

  private OmitTransformation(MappingConfiguration<?, ?> mapping, PropertyDescriptor sourceProperty,
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
  static OmitTransformation omitDestination(MappingConfiguration<?, ?> mapping,
      PropertyDescriptor destinationProperty) {
    denyNull("mapping", mapping);
    denyNull("destinationProperty", destinationProperty);
    return new OmitTransformation(mapping, null, destinationProperty);
  }

  /**
   * @return Returns <code>true</code> if this {@link OmitTransformation} omits a destination property.
   */
  public boolean isOmitInDestination() {
    return nonNull(destinationProperty);
  }

  /**
   * @return Returns <code>true</code> if this {@link OmitTransformation} omits a destination property.
   */
  public boolean isOmitInSource() {
    return nonNull(sourceProperty);
  }

  /**
   * Omits a property in the source object.
   *
   * @param mapping The mapper to link to.
   * @param sourceProperty the property to omit in the source
   * @return Returns a new {@link OmitTransformation}.
   */
  static OmitTransformation omitSource(MappingConfiguration<?, ?> mapping, PropertyDescriptor sourceProperty) {
    denyNull("mapping", mapping);
    denyNull("destinationProperty", sourceProperty);
    return new OmitTransformation(mapping, sourceProperty, null);
  }

  @Override
  protected void performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty, Object destination) throws MappingException {
  }

  @Override
  protected MappedResult performValueTransformation(Object source, Object destination) throws MappingException {
    return MappedResult.skip();
  }

  @Override
  protected void validateTransformation() throws MappingException {
  }

  @Override
  public String toString(boolean detailed) {
    if (this.sourceProperty != null) {
      return String.format(OMITTING_MSG, asString(sourceProperty, detailed));
    } else {
      return String.format(OMITTING_MSG, asString(destinationProperty, detailed));
    }
  }

}
