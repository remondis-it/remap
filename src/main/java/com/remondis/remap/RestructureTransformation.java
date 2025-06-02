package com.remondis.remap;

import static com.remondis.remap.Properties.asString;
import static java.util.Objects.isNull;

import java.beans.PropertyDescriptor;

/**
 * The restructure transformation is used to build a destination object using a mapper root->destination.
 *
 * @param <S> The source type of mapping.
 * @param <D> The destination type of mapping.
 * @param <RD> The type of the destination field.
 * @author schuettec
 */
class RestructureTransformation<S, D, RD> extends Transformation {

  private static final String MSG_MAPPING = "Restructure complex object for field %s using the following mapping\n%s.";
  private static final String MSG_NO_MAPPING = "Restructure complex object for field %s.";

  private Mapper<S, RD> restructureMapper;

  RestructureTransformation(MappingConfiguration<?, ?> mapping, PropertyDescriptor sourceProperty,
      PropertyDescriptor destinationProperty) {
    super(mapping, null, destinationProperty);
  }

  @Override
  protected MappedResult performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty) throws MappingException {
    MappedResult result = performValueTransformation(source);
    return result;
    // if (result.hasValue()) {
    // writeOrFail(destinationProperty, destination, result.getValue());
    // }
  }

  @Override
  protected MappedResult performValueTransformation(Object source) throws MappingException {
    RD destinationValue = null;
    destinationValue = restructureMapper.map((S) source);
    return MappedResult.value(destinationValue);
  }

  @SuppressWarnings("unchecked")
  @Override
  protected void validateTransformation() throws MappingException {
    try {
      this.restructureMapper = (Mapper<S, RD>) mapping.mapper();
    } catch (MappingException e) {
      throw new MappingException(
          "Cannot build mapper for restructuring field " + asString(destinationProperty, true) + ".", e);
    }
  }

  @Override
  public String toString(boolean detailed) {
    if (isNull(restructureMapper)) {
      return String.format(MSG_NO_MAPPING, asString(destinationProperty, detailed));
    } else {
      return String.format(MSG_MAPPING, asString(destinationProperty, detailed), restructureMapper.toString());
    }
  }

  /**
   * @return Returns the {@link Mapper} used to restructure the destination field.
   */
  protected Mapper<S, RD> getRestructureMapper() {
    return restructureMapper;
  }

}
