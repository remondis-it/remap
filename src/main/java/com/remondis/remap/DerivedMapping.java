package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.beans.PropertyDescriptor;
import java.util.Set;

/**
 * This is a subclass of {@link Mapping} that allows re-definition of mapping configurations for fields of the parent
 *
 * @param <S>
 *        source type of the mapping
 *
 * @param <D>
 *        destination type of the mapping
 */
public class DerivedMapping<S, D> extends Mapping<S, D> {

  DerivedMapping(Class<S> derivedSource, Class<D> derivedDestination, Mapping<? super S, ? super D> parentMapping) {
    super(derivedSource, derivedDestination, parentMapping);
  }

  @Override
  public Mapping<S, D> omitInDestination(FieldSelector<D> destinationSelector) {
    denyNull("destinationSelector", destinationSelector);

    PropertyDescriptor propertyDescriptor = getPropertyFromFieldSelector(Target.DESTINATION, OMIT_FIELD_DEST,
        destination, destinationSelector);

    OmitTransformation omitDestination = OmitTransformation.omitDestination(this, propertyDescriptor);

    deleteMapping(mappedDestinationProperties, propertyDescriptor);
    deleteTransformation(omitDestination);

    super.omitInDestination(destinationSelector);
    return this;
  }

  @Override
  public Mapping<S, D> omitInSource(FieldSelector<S> sourceSelector) {
    denyNull("sourceSelector", sourceSelector);
    // Omit in destination
    PropertyDescriptor propertyDescriptor = getPropertyFromFieldSelector(Target.SOURCE, OMIT_FIELD_SOURCE, this.source,
        sourceSelector);

    OmitTransformation omitSource = OmitTransformation.omitSource(this, propertyDescriptor);

    deleteMapping(mappedSourceProperties, propertyDescriptor);
    deleteTransformation(omitSource);

    super.omitInSource(sourceSelector);
    return this;

  }

  @Override
  protected void addMapping(PropertyDescriptor sourceProperty, PropertyDescriptor destProperty,
      Transformation transformation) {

    deleteMapping(mappedSourceProperties, sourceProperty);
    deleteMapping(mappedDestinationProperties, destProperty);
    deleteTransformationByDestination(destProperty);

    super.addMapping(sourceProperty, destProperty, transformation);
  }

  @Override
  protected void addDestinationMapping(PropertyDescriptor destProperty, Transformation setTransformation) {

    deleteMapping(mappedDestinationProperties, destProperty);
    deleteTransformationByDestination(destProperty);

    super.addDestinationMapping(destProperty, setTransformation);
  }

  @Override
  protected void _useMapper(InternalMapper<?, ?> interalMapper) {
    Projection<?, ?> projection = interalMapper.getProjection();
    mappers.remove(projection);
    super._useMapper(interalMapper);
  }

  private void deleteMapping(Set<PropertyDescriptor> mappedDestinationProperties,
      PropertyDescriptor propertyDescriptor) {
    mappedDestinationProperties.remove(propertyDescriptor);
  }

  private void deleteTransformation(Transformation transformation) {
    mappings.remove(transformation);
  }

  private void deleteTransformationByDestination(PropertyDescriptor destProperty) {
    mappings.removeIf(t -> t.getDestinationProperty()
        .equals(destProperty));
  }

}
