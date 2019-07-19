package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;
import static com.remondis.remap.MappingException.alreadyMappedProperty;
import static com.remondis.remap.MappingException.multipleInteractions;
import static com.remondis.remap.MappingException.notAProperty;
import static com.remondis.remap.MappingException.zeroInteractions;
import static com.remondis.remap.Properties.createUnmappedMessage;
import static com.remondis.remap.ReflectionUtil.newInstance;
import static java.util.Objects.nonNull;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Comparator;
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
 * object. To retrieve a valid mapper, all properties must be either mapped/reassigned or omitted.
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
public class Mapping<S, D> {

  static final String OMIT_FIELD_DEST = "omit in destination";
  static final String OMIT_FIELD_SOURCE = "omit in source";

  private Class<S> source;
  private Class<D> destination;

  /**
   * Holds the list of mappers registered for hierarchical mapping.
   */
  private Map<Projection<?, ?>, InternalMapper<?, ?>> mappers;

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

  /**
   * This flag indicates that all other source properties that are not part of the mapping should be omitted. Attention:
   * Do not omit implicit mappings.
   */
  private boolean omitOtherSourceProperties;

  /**
   * This flag indicates that all other destination properties that are not part of the mapping should be omitted.
   * Attention:
   * Do not omit implicit mappings.
   */
  private boolean omitOtherDestinationProperties;

  /**
   * This flag indicates that the creation of implicit mappings should be disabled.
   */
  private boolean noImplicitMappings;

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

    PropertyDescriptor propertyDescriptor = getPropertyFromFieldSelector(Target.DESTINATION, OMIT_FIELD_DEST,
        destination, destinationSelector);
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
   * Omits all unmapped source and destination fields. This method adds the necessary
   * {@link #omitInDestination(FieldSelector)} and {@link #omitInSource(FieldSelector)} declarations to the mapping as
   * if they were called specifically.
   * <p>
   * <b>Note: The use of {@link #omitOthers()} carries the risk of erroneously excluding fields from mapping. For
   * example:
   * If a field is added on the source type, a mapping configuration that does not use {@link #omitOthers()} will
   * complain
   * about a new unmapped field. This normally gives the developer a hint, to either specify a mapping or omit this
   * field intentionally. If this method is used, any unmapped field will be omitted without notification!
   * </b>
   * </p>
   *
   * @return Returns this object for method chaining.
   */
  public Mapping<S, D> omitOthers() {
    omitOtherSourceProperties();
    omitOtherDestinationProperties();
    return this;
  }

  /**
   * Omits all unmapped destination fields. This method adds the necessary
   * {@link #omitInDestination(FieldSelector)} declarations to the mapping as
   * if they were called specifically.
   * <p>
   * <b>Note: The use of {@link #omitOtherDestinationProperties()} carries the risk of erroneously excluding fields from
   * mapping. For
   * example:
   * If a field is added on the destination type, a mapping configuration that does not use
   * {@link #omitOtherDestinationProperties()} will
   * complain
   * about a new unmapped field. This normally gives the developer a hint, to either specify a mapping or omit this
   * field intentionally. If this method is used, any unmapped field will be omitted without notification!
   * </b>
   * </p>
   *
   * @return Returns this object for method chaining.
   */
  public Mapping<S, D> omitOtherDestinationProperties() {
    this.omitOtherDestinationProperties = true;
    return this;
  }

  /**
   * Omits all unmapped source fields. This method adds the necessary
   * {@link #omitInSource(FieldSelector)} declarations to the mapping as
   * if they were called specifically.
   * <p>
   * <b>Note: The use of {@link #omitOtherSourceProperties()} carries the risk of erroneously excluding fields from
   * mapping. For
   * example:
   * If a field is added on the source type, a mapping configuration that does not use
   * {@link #omitOtherSourceProperties()} will
   * complain
   * about a new unmapped field. This normally gives the developer a hint, to either specify a mapping or omit this
   * field intentionally. If this method is used, any unmapped field will be omitted without notification!
   * </b>
   * </p>
   *
   * @return Returns this object for method chaining.
   */
  public Mapping<S, D> omitOtherSourceProperties() {
    this.omitOtherSourceProperties = true;
    return this;
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
    PropertyDescriptor propertyDescriptor = getPropertyFromFieldSelector(Target.SOURCE, OMIT_FIELD_SOURCE, this.source,
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
  public <RS> ReassignBuilder<S, D> reassign(FieldSelector<S> sourceSelector) {
    denyNull("sourceSelector", sourceSelector);
    PropertyDescriptor typedSourceProperty = getPropertyFromFieldSelector(Target.SOURCE, ReassignBuilder.ASSIGN,
        this.source, sourceSelector);
    ReassignBuilder<S, D> reassignBuilder = new ReassignBuilder<>(typedSourceProperty, destination, this);
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

    TypedPropertyDescriptor<RS> sourceProperty = getTypedPropertyFromFieldSelector(Target.SOURCE,
        ReplaceBuilder.TRANSFORM, this.source, sourceSelector);
    TypedPropertyDescriptor<RD> destProperty = getTypedPropertyFromFieldSelector(Target.DESTINATION,
        ReplaceBuilder.TRANSFORM, this.destination, destinationSelector);

    ReplaceBuilder<S, D, RD, RS> builder = new ReplaceBuilder<>(sourceProperty, destProperty, this);
    return builder;
  }

  /**
   * Defines a custom source of a value that should be mapped to the specified property of the destination object.
   * The custom source value is provided by a supplier lambda function. <b>Note: The mapping
   * library is designed to reduce the required client tests. Using this method requires the client
   * to test the supplier lambda function!</b>
   *
   * @param destinationSelector
   *        The {@link FieldSelector}s selecting the destination property with
   *        get-method invocation.
   * @return Returns {@link ReplaceBuilder} to specify the transform function and null-strategy.
   */
  public <RD> SetBuilder<S, D, RD> set(TypedSelector<RD, D> destinationSelector) {
    denyNull("destinationSelector", destinationSelector);
    TypedPropertyDescriptor<RD> destProperty = getTypedPropertyFromFieldSelector(Target.DESTINATION,
        ReplaceBuilder.TRANSFORM, this.destination, destinationSelector);
    SetBuilder<S, D, RD> builder = new SetBuilder<>(destProperty, this);
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
    TypedPropertyDescriptor<Collection<RS>> sourceProperty = getTypedPropertyFromFieldSelector(Target.SOURCE,
        ReplaceBuilder.TRANSFORM, this.source, sourceSelector);
    TypedPropertyDescriptor<Collection<RD>> destProperty = getTypedPropertyFromFieldSelector(Target.DESTINATION,
        ReplaceBuilder.TRANSFORM, this.destination, destinationSelector);

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

  protected void addDestinationMapping(PropertyDescriptor destProperty, Transformation setTransformation) {
    denyAlreadyMappedProperty(mappedDestinationProperties, destProperty);
    // mark the property as mapped in destination
    mappedDestinationProperties.add(destProperty);
    // create omit transformation object
    mappings.add(setTransformation);
  }

  private void denyAlreadyOmittedProperty(PropertyDescriptor sourceProperty) {
    if (mappedSourceProperties.contains(sourceProperty)) {
      // Search for omit-Operations
      mappings.stream()
          .forEach(t -> {
            PropertyDescriptor omitSourceProperty = t.getSourceProperty();
            // If omitSourceProperty is null, then the mapping is an OmitInDestination-Operation.
            if (t instanceof OmitTransformation && nonNull(omitSourceProperty)
                && omitSourceProperty.equals(sourceProperty)) {
              throw alreadyMappedProperty(sourceProperty);
            }
          });
    }
  }

  /**
   * Returns the mapper configured with this builder.
   *
   * @return The mapper instance.
   */
  public Mapper<S, D> mapper() {
    if (!noImplicitMappings) {
      addStrictMapping();
    }

    if (omitOtherSourceProperties) {
      addOmitForSource();
    }
    if (omitOtherDestinationProperties) {
      addOmitForDestination();
    }

    validateMapping();
    return new Mapper<>(this);
  }

  private void addOmitForDestination() {
    Set<PropertyDescriptor> unmappedDestinationProperties = getUnmappedDestinationProperties();
    for (PropertyDescriptor propertyDescriptor : unmappedDestinationProperties) {
      OmitTransformation omitDestination = OmitTransformation.omitDestination(this, propertyDescriptor);
      omitMapping(mappedDestinationProperties, propertyDescriptor, omitDestination);
    }
  }

  private void addOmitForSource() {
    // Get the set of property names
    Set<String> unmappedSourcePropertyNames = getUnmappedSourceProperties().stream()
        .map(PropertyDescriptor::getName)
        .collect(Collectors.toSet());

    // Add a reassign for all properties of source that are unmapped properties in the destination
    getUnmappedSourceProperties().stream()
        .filter(pd -> unmappedSourcePropertyNames.contains(pd.getName()))
        .forEach(pd -> {
          OmitTransformation omitSource = OmitTransformation.omitSource(this, pd);
          omitMapping(mappedSourceProperties, pd, omitSource);
        });
  }

  /**
   * This method adds a strict mapping for all unmapped properties of source that have a
   * corresponding property in the destination type.
   */
  private void addStrictMapping() {
    // Get all unmapped properties from destination because this will be the only properties that can be mapped from
    // source.
    Set<PropertyDescriptor> unmappedDestinationProperties = getUnmappedDestinationProperties();
    // Get the set of property names
    Set<String> unmappedDestinationPropertyNames = unmappedDestinationProperties.stream()
        .map(PropertyDescriptor::getName)
        .collect(Collectors.toSet());
    // Add a reassign for all properties of source that are unmapped properties in the destination
    getUnmappedSourceProperties().stream()
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
    unmapped.addAll(getUnmappedSourceProperties());
    // Check that there are no unmapped destination fields
    unmapped.addAll(getUnmappedDestinationProperties());
    return unmapped;
  }

  private Set<PropertyDescriptor> getUnmappedDestinationProperties() {
    return getUnmappedProperties(destination, mappedDestinationProperties, Target.DESTINATION);
  }

  private Set<PropertyDescriptor> getUnmappedSourceProperties() {
    return getUnmappedProperties(source, mappedSourceProperties, Target.SOURCE);
  }

  /**
   * Returns all properties from the specified type, that were unmapped in the specified {@link Set}
   * of {@link PropertyDescriptor}s.
   *
   * @param type
   *        The type to check for unmapped properties.
   * @param mappedSourceProperties
   *        The set of mapped properties.
   * @param target The type of mapping target.
   * @return Returns the {@link Set} of unmapped properties.
   */
  private <T> Set<PropertyDescriptor> getUnmappedProperties(Class<T> type,
      Set<PropertyDescriptor> mappedSourceProperties, Target targetType) {
    Set<PropertyDescriptor> allSourceProperties = Properties.getProperties(type, targetType);
    allSourceProperties.removeAll(mappedSourceProperties);
    return allSourceProperties;
  }

  /**
   * Executes a {@link FieldSelector} lambda on a proxy object of the specified type and returns the
   * {@link PropertyDescriptor} of the property selected.
   *
   * @param target Defines if the properties are validated against source or target rules.
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
  static <R, T> TypedPropertyDescriptor<R> getTypedPropertyFromFieldSelector(Target target, String configurationMethod,
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
      PropertyDescriptor property = getPropertyDescriptorOrFail(target, sensorType, propertyName);
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
   * @param target Defines if the properties are validated against source or target rules.
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
  static <T> PropertyDescriptor getPropertyFromFieldSelector(Target target, String configurationMethod,
      Class<T> sensorType, FieldSelector<T> selector) {
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
      return getPropertyDescriptorOrFail(target, sensorType, propertyName);
    } else {
      throw zeroInteractions(configurationMethod);
    }
  }

  /**
   * Ensures that the specified property name is a property in the specified {@link Set} of {@link
   * PropertyDescriptor}s.
   *
   * @param target Defines if the properties are validated against source or target rules.
   * @param type
   *        The inspected type.
   * @param propertyName
   *        The property name
   */
  static PropertyDescriptor getPropertyDescriptorOrFail(Target target, Class<?> type, String propertyName) {
    Optional<PropertyDescriptor> property;
    property = Properties.getProperties(type, target)
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
    InternalMapper<?, ?> interalMapper = new MapperAdapter<>(mapper);
    _useMapper(interalMapper);
    return this;
  }

  /**
   * Registers a custom type conversion to this mapping that is to be used whenever a type mapping is required that is
   * not defined by a replace operation.
   * <b>Note: Only one mapper/type converter can be added for a combination of
   * source and destination type!</b>
   *
   * @param typeMapping
   *        A {@link TypeMapping}.
   * @return Returns this {@link Mapping} object for further configuration.
   */
  public Mapping<S, D> useMapper(TypeMapping<?, ?> typeMapping) {
    denyNull("typeMapping", typeMapping);
    _useMapper(typeMapping);
    return this;
  }

  /**
   * Disables the creation of implicit mappings, so that fields with the same name and same type are not mapped
   * automatically any more. This requires the user to define the mappings explicitly using
   * {@link #reassign(FieldSelector)} or any other mapping operation.
   *
   * @return Returns this {@link Mapping} object for further configuration.
   */
  public Mapping<S, D> noImplicitMappings() {
    this.noImplicitMappings = true;
    return this;
  }

  /**
   * Returns <code>true</code> if the mapper does not create implicit mappings. If <code>false</code>
   * is returned, the mapper creates implicit mappings for field that have the same name and type.
   */
  boolean isNoImplicitMappings() {
    return noImplicitMappings;
  }

  private void _useMapper(InternalMapper<?, ?> interalMapper) {
    Projection<?, ?> projection = interalMapper.getProjection();
    if (mappers.containsKey(projection)) {
      throw MappingException.duplicateMapper(projection.getSource(), projection.getDestination());
    } else {
      mappers.put(projection, interalMapper);
    }
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
  <S1, D1> InternalMapper<S1, D1> getMapperFor(Class<S1> sourceType, Class<D1> destinationType) {
    Projection<?, ?> projection = new Projection<>(sourceType, destinationType);
    if (mappers.containsKey(projection)) {
      return (InternalMapper<S1, D1>) mappers.get(projection);
    } else {
      throw MappingException.noMapperFound(sourceType, destinationType);
    }
  }

  /**
   * Checks if the specified mapper is registered.
   *
   * @param sourceType The source type
   * @param destinationType the destination type
   * @return Returns <code>true</code> if a mapper was registered for this type of conversion. Otherwise
   *         <code>false</code>
   *         is returned.
   */
  public <S1, D1> boolean hasMapperFor(Class<S1> sourceType, Class<D1> destinationType) {
    Projection<?, ?> projection = new Projection<>(sourceType, destinationType);
    return mappers.containsKey(projection);
  }

  /**
   * Performs the actual mapping with iteration recursively through the object hierarchy.
   *
   * @param source
   *        The source object to map to a new destination object.
   * @return Returns a newly created destination object.
   */
  D map(S source) {
    return map(source, null);
  }

  /**
   * Performs the actual mapping with iteration recursively through the object hierarchy.
   * Warning, this feature is not provided for nested Collections instances, only for instances and nested instances
   *
   * @param source
   *        The source object to map to a new destination object.
   * @param destination
   *        The destination object to populate
   * @return Returns a newly created destination object.
   */
  D map(S source, D destination) {
    D destinationObject = destination;
    if (source == null) {
      throw MappingException.denyMappingOfNull();
    }
    if (destination == null) {
      destinationObject = createDestination();
    }
    for (Transformation t : mappings) {
      t.performTransformation(source, destinationObject);
    }
    return destinationObject;
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
    return toString(false);
  }

  /**
   * Returns a string representation of this mapper.
   *
   * @param detailed If <code>true</code> the string will be more detailed.
   * @return Returns a string representation of this mapper.
   */
  public String toString(boolean detailed) {
    StringBuilder b = new StringBuilder("Mapping from ").append(detailed ? source.getName() : source.getSimpleName())
        .append("\n\t  to ")
        .append(detailed ? destination.getName() : destination.getSimpleName())
        .append("\n with transformation:\n");

    mappings.stream()
        .sorted(Comparator.comparing(t -> t.getClass()
            .getName()))
        .forEach(t -> {
          b.append("- ")
              .append(t.toString(detailed))
              .append("\n");
        });

    Set<PropertyDescriptor> unmappedProperties = getUnmappedProperties();
    if (unmappedProperties.isEmpty()) {
      b.append("All properties are mapped!");
    } else {
      b.append(createUnmappedMessage(unmappedProperties));
    }
    return b.toString();
  }

}
