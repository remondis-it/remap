package com.remondis.remap;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.remondis.remap.Properties.asString;
import static java.util.Objects.isNull;

class RestructureTransformation<S, D, RD> extends Transformation {

  private static final String MSG_MAPPING = "Restructure complex object for field %s using the following mapping\n%s.";
  private static final String MSG_NO_MAPPING = "Restructure complex object for field %s.";

  private Optional<Supplier<RD>> objectCreator;
  private boolean applyingSpecificConfiguration;
  private Mapper<S, RD> restructureMapper;

  RestructureTransformation(MappingConfiguration<?, ?> mapping, PropertyDescriptor sourceProperty,
      PropertyDescriptor destinationProperty, Supplier<RD> objectCreator, boolean applyingSpecificConfiguration) {
    super(mapping, null, destinationProperty);
    this.objectCreator = Optional.ofNullable(objectCreator);
    this.applyingSpecificConfiguration = applyingSpecificConfiguration;
  }

  @Override
  protected void performTransformation(PropertyDescriptor sourceProperty, Object source,
      PropertyDescriptor destinationProperty, Object destination) throws MappingException {
    RD destinationValue = null;
    if (objectCreator.isPresent()) {
      RD newObject = objectCreator.get()
          .get();
      destinationValue = restructureMapper.map((S) source, newObject);
    } else {
      destinationValue = restructureMapper.map((S) source);
    }
    writeOrFail(destinationProperty, destination, destinationValue);
  }

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

  boolean isApplyingSpecificConfiguration() {
    return applyingSpecificConfiguration;
  }

  public Mapper<S, RD> getRestructureMapper() {
    return restructureMapper;
  }

}
