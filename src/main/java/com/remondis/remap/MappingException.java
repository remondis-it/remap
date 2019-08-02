package com.remondis.remap;

import static com.remondis.remap.Properties.asString;
import static com.remondis.remap.Properties.asStringWithType;
import static com.remondis.remap.Properties.createUnmappedMessage;
import static com.remondis.remap.Properties.getPropertyClass;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Thrown if the mapping configuration has errors or if an actual mapping fails.
 */
public class MappingException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  MappingException() {
    super();
  }

  MappingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  MappingException(String message, Throwable cause) {
    super(message, cause);
  }

  MappingException(String message) {
    super(message);
  }

  MappingException(Throwable cause) {
    super(cause);
  }

  static MappingException zeroInteractions(String configurationMethod) {
    return new MappingException(String
        .format("The field selector for method '%s' tracked zero interactions with properties.", configurationMethod));
  }

  static MappingException multipleInteractions(String configurationMethod, List<String> trackedPropertyNames) {
    return new MappingException(String.format(
        "The field selector for method '%s' tracked multiple interactions with the following properties: %s."
            + " Only one interaction perfield selector is allowed!",
        configurationMethod, String.join(",", trackedPropertyNames)));
  }

  static MappingException notAProperty(Class<?> type, String property) {
    return new MappingException(String.format(
        "The get-method for property '%s' in type %s is not a valid Java Bean property.", property, type.getName()));
  }

  static MappingException notAGetter(Method method) {
    return new MappingException(
        String.format("The method '%s' in type %s is not a valid Java Bean property get-method.", method.getName(),
            method.getDeclaringClass()
                .getName()));
  }

  static MappingException noReturnTypeOnGetter(Method method) {
    return new MappingException(
        String.format("The method '%s' in type '%s' is not a valid getter because it has no return type.",
            method.getName(), method.getDeclaringClass()
                .getName()));
  }

  static MappingException alreadyMappedProperty(PropertyDescriptor propertyDescriptor) {
    return new MappingException(
        String.format("The property '%s' in '%s' is already mapped an cannot be selected more than once.",
            propertyDescriptor.getName(), getPropertyClass(propertyDescriptor)));
  }

  static MappingException unmappedProperties(Set<PropertyDescriptor> unmapped) {
    return new MappingException(createUnmappedMessage(unmapped));
  }

  static MappingException noMapperFound(PropertyDescriptor sourceProperty, Class<?> sourceType,
      PropertyDescriptor destinationProperty, Class<?> destinationType) {
    return new MappingException(String.format(
        "No mapper found for type mapping from %s to %s.\nFor example used by the property mapping from "
            + asString(sourceProperty) + " to " + asString(destinationProperty),
        sourceType.getName(), destinationType.getName()) + ".");
  }

  static MappingException duplicateMapper(Class<?> source, Class<?> destination) {
    return new MappingException(String.format("A mapper mapping the type %s to type %s was already registered.",
        source.getName(), destination.getName()));
  }

  static MappingException incompatiblePropertyTypes(Transformation t, PropertyDescriptor sourceProperty,
      PropertyDescriptor destinationProperty) {
    return new MappingException(String
        .format("The transformation %s cannot be applied on the incompatible property types\n%s\nand %s", t.getClass()
            .getSimpleName(), asStringWithType(sourceProperty), asStringWithType(destinationProperty)));
  }

  static MappingException invocationFailed(PropertyDescriptor property, Exception e) {
    return new MappingException(String.format("Invoking access method for property %s failed.", property), e);
  }

  static MappingException invocationTarget(PropertyDescriptor property, InvocationTargetException e) {
    // Try to get the cause, because the wrapping InvocationTargetException should
    // not appear in stack trace.
    Throwable cause = e.getCause();
    if (cause == null) {
      // If the cause is null, set InvocationTargetException as cause.
      cause = e;
    }
    return new MappingException(
        String.format("An access method for property %s threw an exception.", asString(property)), e);
  }

  static MappingException noDefaultConstructor(Class<?> type) {
    return new MappingException(String.format(
        "The type %s does not have a public no-args constructor and cannot be used for mapping.", type.getName()));
  }

  static MappingException noDefaultConstructor(Class<?> type, Exception e) {
    return new MappingException(String.format(
        "The type %s does not have a public no-args constructor and cannot be used for mapping.", type.getName()), e);
  }

  static MappingException newInstanceFailed(Class<?> type, Exception e) {
    return new MappingException(String.format("Creating a new instance of type %s failed.", type.getName()), e);
  }

  static MappingException unsupportedCollection(Collection<?> collection) {
    return new MappingException(String.format(
        "The collection '%s' is currently not supported. Only java.util.Set and java.util.List"
            + " are supported collections.",
        collection.getClass()
            .getName()));
  }

  static MappingException denyMappingOfNull() {
    return new MappingException("Mapper cannot map null object.");
  }

  static MappingException unsupportedCollection(Class<?> collectionType) {
    return new MappingException(String.format("The collection type %s is unsupported.", collectionType.getName()));
  }

  static MappingException incompatibleCollectionMapping(PropertyDescriptor sourceProperty,
      GenericParameterContext sourceCtx, PropertyDescriptor destinationProperty, GenericParameterContext destCtx) {
    GenericParameterContext rootSrcCtx = new GenericParameterContext(sourceProperty.getReadMethod());
    GenericParameterContext rootDestCtx = new GenericParameterContext(destinationProperty.getReadMethod());
    StringBuilder builder = new StringBuilder("Incompatible nested collections found mapping\n\t");
    builder.append(asString(sourceProperty))
        .append(" to ~>\n\t")
        .append(asString(destinationProperty))
        .append("\nCannot map ")
        .append(sourceCtx.getCurrentType()
            .getSimpleName())
        .append(" to ")
        .append(destCtx.getCurrentType()
            .getSimpleName())
        .append(".\n")
        .append("Use replace for manual mapping!\n")
        .append("\nType nesting is\n\t")
        .append("-> in source type: ")
        .append("\n\t")
        .append(rootSrcCtx.get()
            .toString())
        .append("\n\t-> in destination type: ")
        .append("\n\t")
        .append(rootDestCtx.get()
            .toString())
        .append("\n\tcannot map \n\t")
        .append(sourceCtx.get()
            .toString())
        .append("\n\tto\n\t")
        .append(destCtx.get()
            .toString());
    return new MappingException(builder.toString());
  }

}
