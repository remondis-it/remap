package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;
import static com.remondis.remap.MappingException.alreadyMappedProperty;
import static com.remondis.remap.MappingException.multipleInteractions;
import static com.remondis.remap.MappingException.notAProperty;
import static com.remondis.remap.MappingException.zeroInteractions;
import static com.remondis.remap.Properties.createUnmappedMessage;
import static com.remondis.remap.ReflectionUtil.newInstance;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The {@link Mapping} object is used to specify the mapping of the fields from a source object/type
 * to a destination object/type. Only properties can be mapped. Properties are defined with the Java
 * Bean Convention:
 * <ul>
 * <li>A property is a field with any visibility</li>
 * <li>A property has a
 * public getter/setter pair exactly named as the field</li>
 * <li>Boolean values have is/setter
 * methods.</li>
 * <li>A bean has a default zero-args constructor.</li>
 * </ul>
 * For the mapping,
 * keywords like <code>transient</code> do not have any effect.
 *
 * <p>
 * The mapper always tries to map all properties with name from the source to the destination
 * object. To retrieve a valid mapper, all properties must be either mapped/reassinged or omitted.
 * If there are unmapped properties in source/destination type, the {@link Mapping#mapper()} throws
 * an error. If the mapping contains nested mappings of other complex types, a delegation mapper
 * must be registered using {@link #useMapper(Mapper)}, otherweise a {@link MappingException} is
 * thrown.
 * </p>
 *
 * @param <S>
 *        source type of the mapping
 * @param <D>
 *        destination type of the mapping
 * @author schuettec
 */
public final class Mapping<S, D> {

  static final String OMIT_FIELD_DEST = "omit in destination";
  static final String OMIT_FIELD_SOURCE = "omit in source";

  private Class<S> source;
  private Class<D> destination;

  /**
   * Holds the list of mappers registered for hierarchical mapping.
   */
  private Map<Projection<?, ?>, Mapper<?, ?>> mappers;

  /**
   * Holds the list of mapping operations.
   */
  private Set<Transformation> mappings;

  /**
   * This set keeps track of the mapped source properties.
   */
  private Set<PropertyDescriptor> mappedSourceProperties;

  /**
   * This set keeps track of the mapped destination properties.
   */
  private Set<PropertyDescriptor> mappedDestinationProperties;

  Mapping(Class<S> source, Class<D> destination) {
    this.source = source;
    this.destination = destination;
    this.mappings = new HashSet<>();
    this.mappedSourceProperties = new HashSet<>();
    this.mappedDestinationProperties = new HashSet<>();
    this.mappers = new Hashtable<>();
  }

  /**
   * Specifies the source data type to map from.
   *
   * @param source
   *        the data source type.
   * @return Returns a {@link Types} object for further mapping configuration.
   */
  public static <S> Types<S> from(Class<S> source) {
    return new Types<>(source);
  }

  /**
   * Marks a destination field as omitted. The mapping will not touch this field in the destination
   * object.
   *
   * @param destinationSelector
   *        The {@link FieldSelector} lambda that selects the field with invoking
   *        the corresponding getter method.
   * @return Returns this object for method chaining.
   */
  public Mapping<S, D> omitInDestination(FieldSelector<D> destinationSelector) {
    denyNull("destinationSelector", destinationSelector);

    PropertyDescriptor propertyDescriptor = getPropertyFromFieldSelector(OMIT_FIELD_DEST, destination,
        destinationSelector);
    OmitTransformation omitDestination = OmitTransformation.omitDestination(this, propertyDescriptor);
    omitMapping(mappedDestinationProperties, propertyDescriptor, omitDestination);
    return this;
  }

  private void omitMapping(Set<PropertyDescriptor> mappedDestinationProperties, PropertyDescriptor propertyDescriptor,
      OmitTransformation omitDestination) {
    // check if the property descriptor is already mapped
    denyAlreadyMappedProperty(mappedDestinationProperties, propertyDescriptor);
    // mark the property as mapped in destination
    mappedDestinationProperties.add(propertyDescriptor);
    // create omit transformation object
    mappings.add(omitDestination);
  }

  /**
   * Marks a source field as omitted. The mapping will not touch this field in the source object.
   *
   * @param sourceSelector
   *        The {@link FieldSelector} lambda that selects the field with invoking the
   *        corresponding getter method.
   * @return Returns this object for method chaining.
   */
  public Mapping<S, D> omitInSource(FieldSelector<S> sourceSelector) {
    denyNull("sourceSelector", sourceSelector);
    // Omit in destination
    PropertyDescriptor propertyDescriptor = getPropertyFromFieldSelector(OMIT_FIELD_SOURCE, this.source,
        sourceSelector);
    OmitTransformation omitSource = OmitTransformation.omitSource(this, propertyDescriptor);
    omitMapping(mappedSourceProperties, propertyDescriptor, omitSource);
    return this;
  }

  /**
   * Reassigns a property from the source to the specified property of the destination object.
   *
   * @param sourceSelector
   *        The {@link FieldSelector}s selecting the source property with get-method
   *        invocation.
   * @return Returns a {@link ReassignBuilder} to specify the destination field.
   */
  public <RS> ReassignBuilder<S, D, RS> reassign(TypedSelector<RS, S> sourceSelector) {
    denyNull("sourceSelector", sourceSelector);

    TypedPropertyDescriptor<RS> typedSourceProperty = getTypedPropertyFromFieldSelector(ReassignBuilder.ASSIGN,
        this.source, sourceSelector);
    ReassignBuilder<S, D, RS> reassignBuilder = new ReassignBuilder<>(typedSourceProperty, destination, this);
    return reassignBuilder;
  }

  /**
   * Maps a property from the source to the specified property of the destination object with
   * transforming the source value using the specified transform lambda. <b>Note: The mapping
   * library is designed to reduce the required client tests. Using this method requires the client
   * to test the transformation function!</b>
   *
   * @param sourceSelector
   *        The {@link FieldSelector}s selecting the source property with get-method
   *        invocation.
   * @param destinationSelector
   *        The {@link FieldSelector}s selecting the destination property with
   *        get-method invocation.
   * @return Returns {@link ReplaceBuilder} to specify the transform function and null-strategy.
   */
  public <RD, RS> ReplaceBuilder<S, D, RD, RS> replace(TypedSelector<RS, S> sourceSelector,
      TypedSelector<RD, D> destinationSelector) {
    denyNull("sourceSelector", sourceSelector);
    denyNull("destinationSelector", destinationSelector);

    TypedPropertyDescriptor<RS> sourceProperty = getTypedPropertyFromFieldSelector(ReplaceBuilder.TRANSFORM,
        this.source, sourceSelector);
    TypedPropertyDescriptor<RD> destProperty = getTypedPropertyFromFieldSelector(ReplaceBuilder.TRANSFORM,
        this.destination, destinationSelector);

    ReplaceBuilder<S, D, RD, RS> builder = new ReplaceBuilder<>(sourceProperty, destProperty, this);
    return builder;
  }

  /**
   * Maps a property holding a collection from the source to the specified property holding a collection of the
   * destination object. The specified transform function will be applied on every item in the source value to convert
   * to the specified destination type. <b>Note: The mapping
   * library is designed to reduce the required client tests. Using this method requires the client
   * to test the transformation function!</b>
   *
   * @param sourceSelector
   *        The {@link FieldSelector}s selecting the source property holding a {@link Collection}.
   * @param destinationSelector
   *        The {@link FieldSelector}s selecting the destination property holding a {@link Collection}.
   * @return Returns {@link ReplaceBuilder} to specify the transform function and null-strategy.
   *
   */
  public <RD, RS> ReplaceCollectionBuilder<S, D, RD, RS> replaceCollection(
      TypedSelector<Collection<RS>, S> sourceSelector, TypedSelector<Collection<RD>, D> destinationSelector) {
    denyNull("sourceSelector", sourceSelector);
    denyNull("destinationSelector", destinationSelector);
    TypedPropertyDescriptor<Collection<RS>> sourceProperty = getTypedPropertyFromFieldSelector(ReplaceBuilder.TRANSFORM,
        this.source, sourceSelector);
    TypedPropertyDescriptor<Collection<RD>> destProperty = getTypedPropertyFromFieldSelector(ReplaceBuilder.TRANSFORM,
        this.destination, destinationSelector);

    ReplaceCollectionBuilder<S, D, RD, RS> builder = new ReplaceCollectionBuilder<>(sourceProperty, destProperty, this);
    return builder;
  }

  protected void addMapping(PropertyDescriptor sourceProperty, PropertyDescriptor destProperty,
      Transformation transformation) {
    // check if the property descriptor is already mapped
    denyAlreadyOmittedProperty(sourceProperty);
    denyAlreadyMappedProperty(mappedDestinationProperties, destProperty);
    // mark the property as mapped in destination
    mappedSourceProperties.add(sourceProperty);
    mappedDestinationProperties.add(destProperty);
    // create omit transformation object
    mappings.add(transformation);
  }

  private void denyAlreadyOmittedProperty(PropertyDescriptor sourceProperty) {
    if (mappedSourceProperties.contains(sourceProperty)) {
      // Search for omit-Operations
      mappings.stream()
          .forEach(t -> {
            if (t instanceof OmitTransformation && t.getSourceProperty()
                .equals(sourceProperty)) {
              throw alreadyMappedProperty(sourceProperty);
            }
          });
    }
  }

  /**
   * @return Returns the mapper configured with this builder.
   */
  public Mapper<S, D> mapper() {
    addStrictMapping();
    validateMapping();
    return new Mapper<>(this);
  }

  /**
   * This method adds a strict mapping for all unmapped properties of source that have a
   * corresponding property in the destination type.
   */
  private void addStrictMapping() {
    // Get all unmapped properties from destination because this will be the only properties that can be mapped from
    // source.
    Set<PropertyDescriptor> unmappedDestinationProperties = getUnmappedProperties(destination,
        mappedDestinationProperties);
    // Get the set of property names
    Set<String> unmappedDestinationPropertyNames = unmappedDestinationProperties.stream()
        .map(PropertyDescriptor::getName)
        .collect(Collectors.toSet());
    // Add a reassign for all properties of source that are unmapped properties in the destination
    getUnmappedProperties(source, mappedSourceProperties).stream()
        .filter(pd -> unmappedDestinationPropertyNames.contains(pd.getName()))
        .forEach(pd -> {
          // find the corresponding PropertyDescriptor in the unmapped
          // destination properties and add reassign
          // transformation
          PropertyDescriptor destinationProperty = getPropertyDescriptorByPropertyName(unmappedDestinationProperties,
              pd.getName());
          MapTransformation transformation = new MapTransformation(this, pd, destinationProperty);
          addMapping(pd, destinationProperty, transformation);
        });

  }

  private PropertyDescriptor getPropertyDescriptorByPropertyName(Set<PropertyDescriptor> descriptors,
      String propertyName) {
    Set<PropertyDescriptor> matchedPropertiesByName = descriptors.stream()
        .filter(pd -> pd.getName()
            .equals(propertyName))
        .collect(Collectors.toSet());
    if (matchedPropertiesByName.isEmpty() || matchedPropertiesByName.size() > 1) {
      throw new MappingException(
          String.format("Cannot assign source property '%s' to destination, but this was determined "
              + "to be possible - this is an implementation fault.", propertyName));
    } else {
      return matchedPropertiesByName.iterator()
          .next();
    }
  }

  private void validateMapping() {
    // check for unmapped properties
    Set<PropertyDescriptor> unmapped = getUnmappedProperties();
    if (!unmapped.isEmpty()) {
      throw MappingException.unmappedProperties(unmapped);
    }

    // check if all mappers are available to perform nested mapping
    for (Transformation t : mappings) {
      t.validateTransformation();
    }
  }

  private Set<PropertyDescriptor> getUnmappedProperties() {
    Set<PropertyDescriptor> unmapped = new HashSet<>();
    // Check that there are no unmapped source fields
    unmapped.addAll(getUnmappedProperties(source, mappedSourceProperties));
    // Check that there are no unmapped destination fields
    unmapped.addAll(getUnmappedProperties(destination, mappedDestinationProperties));
    return unmapped;
  }

  /**
   * Returns all properties from the specified type, that were unmapped in the specified {@link Set}
   * of {@link PropertyDescriptor}s.
   *
   * @param type
   *        The type to check for unmapped properties.
   * @param mappedSourceProperties
   *        The set of mapped properties.
   * @return Returns the {@link Set} of unmapped properties.
   */
  private <T> Set<PropertyDescriptor> getUnmappedProperties(Class<T> type,
      Set<PropertyDescriptor> mappedSourceProperties) {
    Set<PropertyDescriptor> allSourceProperties = Properties.getProperties(type);
    allSourceProperties.removeAll(mappedSourceProperties);
    return allSourceProperties;
  }

  /**
   * Executes a {@link FieldSelector} lambda on a proxy object of the specified type and returns the
   * {@link PropertyDescriptor} of the property selected.
   *
   * @param configurationMethod
   *        The configuration method this {@link PropertyDescriptor} is used for. Only needed for exception messages.
   * @param sensorType
   *        The type of sensor object.
   * @param selector
   *        The selector lambda.
   * @return Returns the {@link PropertyDescriptor} selected by the lambda.
   * @throws MappingException
   *         if a property was specified for mapping but not invoked.
   */
  static <R, T> TypedPropertyDescriptor<R> getTypedPropertyFromFieldSelector(String configurationMethod,
      Class<T> sensorType, TypedSelector<R, T> selector) {
    InvocationSensor<T> invocationSensor = new InvocationSensor<T>(sensorType);
    T sensor = invocationSensor.getSensor();
    // perform the selector lambda on the sensor
    R returnValue = selector.selectField(sensor);
    // if any property interaction was tracked...
    if (invocationSensor.hasTrackedProperties()) {
      // ...make sure it was exactly one property interaction
      List<String> trackedPropertyNames = invocationSensor.getTrackedPropertyNames();
      denyMultipleInteractions(configurationMethod, trackedPropertyNames);
      // get the property name
      String propertyName = trackedPropertyNames.get(0);
      // find the property descriptor or fail with an exception
      PropertyDescriptor property = getPropertyDescriptorOrFail(sensorType, propertyName);
      TypedPropertyDescriptor<R> tpd = new TypedPropertyDescriptor<R>();
      tpd.returnValue = returnValue;
      tpd.property = property;
      return tpd;
    } else {
      throw zeroInteractions(configurationMethod);
    }
  }

  /**
   * Executes a {@link FieldSelector} lambda on a proxy object of the specified type and returns the
   * {@link PropertyDescriptor} of the property selected.
   *
   * @param configurationMethod
   *        The configuration method this {@link PropertyDescriptor} is used
   *        for. Only needed for exception messages.
   * @param sensorType
   *        The type of sensor object.
   * @param selector
   *        The selector lambda.
   * @return Returns the {@link PropertyDescriptor} selected with the lambda.
   * @throws MappingException
   *         if a property was specified for mapping but not invoked.
   */
  static <T> PropertyDescriptor getPropertyFromFieldSelector(String configurationMethod, Class<T> sensorType,
      FieldSelector<T> selector) {
    InvocationSensor<T> invocationSensor = new InvocationSensor<T>(sensorType);
    T sensor = invocationSensor.getSensor();
    // perform the selector lambda on the sensor
    selector.selectField(sensor);
    // if any property interaction was tracked...
    if (invocationSensor.hasTrackedProperties()) {
      // ...make sure it was exactly one property interaction
      List<String> trackedPropertyNames = invocationSensor.getTrackedPropertyNames();
      denyMultipleInteractions(configurationMethod, trackedPropertyNames);
      // get the property name
      String propertyName = trackedPropertyNames.get(0);
      // find the property descriptor or fail with an exception
      return getPropertyDescriptorOrFail(sensorType, propertyName);
    } else {
      throw zeroInteractions(configurationMethod);
    }
  }

  /**
   * Ensures that the specified property name is a property in the specified {@link Set} of {@link
   * PropertyDescriptor}s.
   *
   * @param type
   *        The inspected type.
   * @param propertyName
   *        The property name
   */
  static PropertyDescriptor getPropertyDescriptorOrFail(Class<?> type, String propertyName) {
    Optional<PropertyDescriptor> property;
    property = Properties.getProperties(type)
        .stream()
        .filter(pd -> pd.getName()
            .equals(propertyName))
        .findFirst();
    if (property.isPresent()) {
      return property.get();
    } else {
      throw notAProperty(type, propertyName);
    }

  }

  static void denyMultipleInteractions(String configurationMethod, List<String> trackedPropertyNames) {
    if (trackedPropertyNames.size() > 1) {
      throw multipleInteractions(configurationMethod, trackedPropertyNames);
    }
  }

  static void denyAlreadyMappedProperty(Set<PropertyDescriptor> mappedProperties,
      PropertyDescriptor propertyDescriptor) {
    if (mappedProperties.contains(propertyDescriptor)) {
      throw alreadyMappedProperty(propertyDescriptor);
    }
  }

  /**
   * Registers a configured mapper to this object that is to be used whenever a hierarchical mapping
   * tries to map the specified types. <b>Note: Only one mapper can be added for a combination of
   * source and destination type!</b>
   *
   * @param mapper
   *        A mapper
   * @return Returns this {@link Mapping} object for further configuration.
   */
  public Mapping<S, D> useMapper(Mapper<?, ?> mapper) {
    denyNull("mapper", mapper);
    Class<?> source = mapper.getMapping()
        .getSource();
    Class<?> destination = mapper.getMapping()
        .getDestination();

    Projection<?, ?> projection = new Projection<>(source, destination);
    if (mappers.containsKey(projection)) {
      throw MappingException.duplicateMapper(source, destination);
    } else {
      mappers.put(projection, mapper);
    }
    return this;
  }

  /**
   * Returns a registered mapper for hierarchical mapping. If the desired mapper was not found a
   * {@link MappingException} is thrown.
   *
   * @param sourceType
   *        The source type
   * @param destinationType
   *        The destination type
   * @return Returns the registered mapper.
   */
  @SuppressWarnings("unchecked")
  <S1, D1> Mapper<S1, D1> getMapperFor(Class<S1> sourceType, Class<D1> destinationType) {
    Projection<?, ?> projection = new Projection<>(sourceType, destinationType);
    if (mappers.containsKey(projection)) {
      return (Mapper<S1, D1>) mappers.get(projection);
    } else {
      throw MappingException.noMapperFound(sourceType, destinationType);
    }
  }

  /**
   * Performs the actual mapping by iterating recursively through the object hierarchy.
   *
   * @param source
   *        The source object to map to a new destination object.
   * @return Returns a newly created destination object.
   */
  D map(S source) {
    if (source == null) {
      throw MappingException.denyMappingOfNull();
    }
    D destinationObject = createDestination();
    for (Transformation t : mappings) {
      t.performTransformation(source, destinationObject);
    }
    return destinationObject;
  }

  /**
   * Performs the actual mapping by iterating recursively through the object hierarchy.
   *
   * @param source
   *        The source object to map to a new destination object.
   * @param destination The destination object to map to.
   */
  void map(S source, D destination) {
    if (source == null) {
      throw MappingException.denyMappingOfNull();
    }
    if (destination == null) {
      throw MappingException.denyMappingToNull();
    }
    for (Transformation t : mappings) {
      t.performTransformation(source, destination);
    }
  }

  private D createDestination() {
    return newInstance(destination);
  }

  Class<S> getSource() {
    return source;
  }

  Class<D> getDestination() {
    return destination;
  }

  Set<Transformation> getMappings() {
    return new HashSet<>(mappings);
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder("Mapping from ").append(source.getName())
        .append("\n\t  to ")
        .append(destination.getName())
        .append("\n with transformation:\n");

    for (Transformation t : mappings) {
      b.append("- ")
          .append(t.toString())
          .append("\n");
    }

    Set<PropertyDescriptor> unmappedProperties = getUnmappedProperties();
    if (unmappedProperties.isEmpty()) {
      b.append("All properties are mapped!");
    } else {
      b.append(createUnmappedMessage(unmappedProperties));
    }
    return b.toString();
  }

}
