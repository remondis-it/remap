package com.remondis.remap;

import static com.remondis.remap.Properties.asString;
import static java.util.Objects.isNull;

import java.beans.PropertyDescriptor;
import java.util.Optional;
import java.util.function.Supplier;

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
    MappedResult result = performValueTransformation(source, destination);
    if (result.hasValue()) {
      writeOrFail(destinationProperty, destination, result.getValue());
    }
  }

  @Override
  protected MappedResult performValueTransformation(Object source, Object destination) throws MappingException {
    RD destinationValue = null;
    if (objectCreator.isPresent()) {
      RD newObject = objectCreator.get()
          .get();
      destinationValue = restructureMapper.map((S) source, newObject);
    } else {
      destinationValue = restructureMapper.map((S) source);
    }
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
   * @return Returns <code>true</code> if the restructure transformation applies further mapping configurations,
   *         otherwise <code>false</code> is returned. Used for {@link RestructureAssertBuilder}.
   */
  boolean isApplyingSpecificConfiguration() {
    return applyingSpecificConfiguration;
  }

  /**
   * @return Returns the {@link Mapper} used to restructure the destination field.
   */
  Mapper<S, RD> getRestructureMapper() {
    return restructureMapper;
  }

}
