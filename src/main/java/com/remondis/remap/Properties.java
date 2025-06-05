package com.remondis.remap;

import static com.remondis.remap.ReflectionUtil.isGetter;
import static com.remondis.remap.ReflectionUtil.isSetter;
import static com.remondis.remap.ReflectionUtil.toPropertyName;
import static java.util.Objects.isNull;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Util class to get a list of all properties of a class.
 *
 * @author schuettec
 */
class Properties {

  /**
   * A readable string representation for a {@link PropertyDescriptor}.
   *
   * @param pd The pd
   * @return Returns a readable string.
   */
  static String asStringWithType(PropertyDescriptor pd) {
    return asStringWithType(pd, false);
  }

  /**
   * A readable string representation for a {@link PropertyDescriptor}.
   *
   * @param pd The pd
   * @param detailed If <code>false</code> simple names should be used. If <code>true</code> fully qualified names
   *        should be used.
   * @return Returns a readable string.
   */
  static String asStringWithType(PropertyDescriptor pd, boolean detailed) {
    Class<?> clazz = Properties.getPropertyClass(pd);
    return String.format("Property '%s' (%s) in %s", pd.getName(), pd.getPropertyType()
        .getName(), (detailed ? clazz.getName() : clazz.getSimpleName()));
  }

  /**
   * A readable string representation for a {@link PropertyDescriptor}.
   *
   * @param pd The pd
   * @return Returns a readable string.
   */
  static String asString(PropertyDescriptor pd) {
    return asString(pd, false);
  }

  /**
   * A readable string representation for a {@link PropertyDescriptor}.
   *
   * @param pd The pd
   * @param detailed If <code>false</code> simple names should be used. If <code>true</code> fully qualified names
   *        should be used.
   * @return Returns a readable string.
   */
  static String asString(PropertyDescriptor pd, boolean detailed) {
    Class<?> clazz = Properties.getPropertyClass(pd);
    return String.format("Property '%s' in %s", pd.getName(), (detailed ? clazz.getName() : clazz.getSimpleName()));
  }

  /**
   * Returns the class declaring the property.
   *
   * @param propertyDescriptor the {@link PropertyDescriptor}
   * @return Returns the declaring class.
   */
  static Class<?> getPropertyClass(PropertyDescriptor propertyDescriptor) {
    return propertyDescriptor.getReadMethod()
        .getDeclaringClass();
  }

  /**
   * Creates a message showing all currently unmapped properties.
   *
   * @param unmapped The set of unmapped properties.
   * @return Returns the message.
   */
  static String createUnmappedMessage(Set<PropertyDescriptor> unmapped) {
    StringBuilder msg = new StringBuilder("The following properties are unmapped:\n");
    for (PropertyDescriptor pd : unmapped) {
      String getter = pd.getReadMethod()
          .getName();
      Method writeMethod = pd.getWriteMethod();
      String setter = isNull(writeMethod) ? "none" : writeMethod.getName();
      msg.append("- ")
          .append(asString(pd))
          .append("\n\taccess methods: ")
          .append(getter)
          .append("() / ")
          .append(setter)
          .append("()\n");
    }
    return msg.toString();
  }

  /**
   * Returns a {@link Set} of properties with read and write access.
   *
   * @param inspectType The type to inspect.
   * @param targetType The type of mapping target.
   * @param fluentSetters if true, setters that return a value are allowed in the mapping.
   * @return Returns the list of {@link PropertyDescriptor}s that grant read and write access.
   * @throws MappingException Thrown on any introspection error.
   */
  static Set<PropertyDescriptor> getProperties(Class<?> inspectType, Target targetType, boolean fluentSetters) {
    try {
      Set<PropertyDescriptor> result = null;
      if (inspectType.isRecord()) {
        result = getPropertiesFromRecord(inspectType);
      } else {
        result = extractBaseProperties(inspectType, targetType, fluentSetters);
        mergeInterfaceProperties(inspectType, result);
        return result;
      }
      return result;
    } catch (IntrospectionException e) {
      throw new MappingException(String.format("Cannot introspect the type %s.", inspectType.getName()));
    }
  }

  /**
   * Extracts the initial set of properties using JavaBeans introspection.
   */
  private static Set<PropertyDescriptor> extractBaseProperties(Class<?> type, Target targetType, boolean fluentSetters)
      throws IntrospectionException {

    BeanInfo beanInfo = Introspector.getBeanInfo(type);
    Stream<PropertyDescriptor> stream = Arrays.stream(beanInfo.getPropertyDescriptors());

    if (fluentSetters && targetType == Target.DESTINATION) {
      stream = stream.map(pd -> pd.getWriteMethod() == null ? checkForAndSetFluentWriteMethod(type, pd) : pd);
    }

    return stream.filter(pd -> !"class".equals(pd.getName()))
        .filter(Properties::hasGetter)
        .filter(pd -> Target.SOURCE.equals(targetType) || hasSetter(pd))
        .collect(Collectors.toSet());
  }

  /**
   * Merges properties from implemented interfaces into the existing property set.
   */
  private static void mergeInterfaceProperties(Class<?> inspectType, Set<PropertyDescriptor> result) {
    for (Class<?> iface : inspectType.getInterfaces()) {
      Map<String, GetterSetterHolder> accessorsByProperty = new HashMap<>();

      for (Method method : iface.getDeclaredMethods()) {
        if (Modifier.isStatic(method.getModifiers())) {
          continue;
        }
        boolean isGetter = isGetter(method);
        boolean isSetter = isSetter(method);
        if (!isGetter && !isSetter) {
          continue;
        }

        String name = toPropertyName(method);
        GetterSetterHolder holder = accessorsByProperty.computeIfAbsent(name, k -> new GetterSetterHolder());

        if (isGetter)
          holder.setGetter(method);
        if (isSetter)
          holder.setSetter(method);
      }

      for (Map.Entry<String, GetterSetterHolder> entry : accessorsByProperty.entrySet()) {
        String name = entry.getKey();
        GetterSetterHolder holder = entry.getValue();

        PropertyDescriptor existing = findPropertyDescriptor(result, name);

        if (existing == null) {
          try {
            PropertyDescriptor pd = new PropertyDescriptor(name, holder.getGetter(), holder.getSetter());
            result.add(pd);
          } catch (IntrospectionException e) {
            throw new MappingException("Failed to create interface PropertyDescriptor for: " + name, e);
          }
        } else {
          result.remove(existing);
          Method getter = chooseGetter(existing.getReadMethod(), holder.getGetter());
          Method setter = chooseSetter(existing.getWriteMethod(), holder.getSetter());
          try {
            result.add(new PropertyDescriptor(name, getter, setter));
          } catch (IntrospectionException e) {
            throw new MappingException("Failed to create PropertyDescriptor for: " + name, e);
          }
        }
      }
    }
  }

  /**
   * Selects a getter method preferring compatible interface override if available.
   */
  private static Method chooseGetter(Method oldGetter, Method newGetter) {
    if (newGetter == null)
      return oldGetter;
    if (oldGetter == null)
      return newGetter;

    Type oldType = oldGetter.getGenericReturnType();
    Type newType = newGetter.getGenericReturnType();

    if (oldType instanceof Class<?> && newType instanceof Class<?> && ((Class<?>) oldType).getName()
        .equals(((Class<?>) newType).getName())) {
      return newGetter;
    }

    return oldGetter;
  }

  /**
   * Selects a setter method preferring compatible interface override if available.
   */
  private static Method chooseSetter(Method oldSetter, Method newSetter) {
    if (newSetter == null)
      return oldSetter;
    if (oldSetter == null)
      return newSetter;

    if (oldSetter.getParameterCount() == 1 && newSetter.getParameterCount() == 1) {
      Type oldType = oldSetter.getGenericParameterTypes()[0];
      Type newType = newSetter.getGenericParameterTypes()[0];

      if (oldType instanceof Class<?> && newType instanceof Class<?> && ((Class<?>) oldType).getName()
          .equals(((Class<?>) newType).getName())) {
        return newSetter;
      }
    }

    return oldSetter;
  }

  /**
   * Finds a PropertyDescriptor object in the existing set list
   */
  private static PropertyDescriptor findPropertyDescriptor(Set<PropertyDescriptor> properties, String propertyName) {
    return properties.stream()
        .filter(pd -> pd.getName()
            .equals(propertyName))
        .findFirst()
        .orElse(null);
  }

  /**
   * Tries to see if a fluent setXXX method exists even though it was not found by the initial retrospection.
   * If a setter exists set it as the property descriptors write method.
   */
  static PropertyDescriptor checkForAndSetFluentWriteMethod(Class<?> inspectType, PropertyDescriptor pd) {
    String writeMethodName = pd.getName();
    writeMethodName = "set" + writeMethodName.substring(0, 1)
        .toUpperCase() + writeMethodName.substring(1);
    try {
      Method setMethod = inspectType.getDeclaredMethod(writeMethodName, pd.getPropertyType());
      if (Modifier.isPublic(setMethod.getModifiers())) {
        /*
         * Create a new PropertyDescriptor instance, because the one supplied here comes from the Java Introspector and
         * is cached VM-wide (?). Due to this caching, it is not possible to deactivate fluent setters for other mapper
         * instances. We have to create a new PropertyDescriptor to avoid this.
         */
        PropertyDescriptor clone = new PropertyDescriptor(pd.getName(), pd.getReadMethod(), setMethod);
        return clone;
      }
    } catch (NoSuchMethodException e) {
      // just ignore, the method does not have to exist
    } catch (IntrospectionException e) {
      throw new RuntimeException(e);
    }
    return pd;
  }

  private static Set<PropertyDescriptor> getPropertiesFromRecord(Class<?> inspectType) {
    Set<PropertyDescriptor> result;
    RecordComponent[] recordComponents = inspectType.getRecordComponents();
    result = Arrays.stream(recordComponents)
        .map(rc -> {
          Method accessorMethod = rc.getAccessor();
          String propertyName = rc.getName();
          PropertyDescriptor pd;
          try {
            pd = new PropertyDescriptor(propertyName, accessorMethod, null);
            return pd;
          } catch (IntrospectionException e) {
            throw MappingException.introspectionFailed(inspectType, e);
          }
        })
        .collect(Collectors.toSet());
    return result;
  }

  private static boolean hasGetter(PropertyDescriptor pd) {
    return pd.getReadMethod() != null;
  }

  private static boolean hasSetter(PropertyDescriptor pd) {
    return pd.getWriteMethod() != null;
  }

  private static class GetterSetterHolder {
    private Method getter;
    private Method setter;

    public Method getGetter() {
      return getter;
    }

    public void setGetter(Method getter) {
      this.getter = getter;
    }

    public Method getSetter() {
      return setter;
    }

    public void setSetter(Method setter) {
      this.setter = setter;
    }
  }

}
