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
import java.util.Arrays;
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
      BeanInfo beanInfo = Introspector.getBeanInfo(inspectType);
      PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
      Stream<PropertyDescriptor> stream = Arrays.asList(propertyDescriptors)
          .stream();
      // Scan the property descriptors if support fluent setters are enabled and add the fluent setter-method.
      if (fluentSetters && targetType == Target.DESTINATION) {
        stream = stream.map(pd -> {
          if (pd.getWriteMethod() == null) {
            // if the fluent setter feature is activated, the property descriptor is replaces on demand, to also reflect
            // setters with a return value.
            return checkForAndSetFluentWriteMethod(inspectType, pd);
          } else {
            return pd;
          }
        });
      }
      Set<PropertyDescriptor> result = stream.filter(pd -> !pd.getName()
          .equals("class"))
          .filter(Properties::hasGetter)
          .filter(pd -> {
            if (Target.SOURCE.equals(targetType)) {
              return true;
            } else {
              return hasSetter(pd);
            }
          })
          .collect(Collectors.toSet());

      for (Class<?> iface : inspectType.getInterfaces()) {
        for (Method method : iface.getDeclaredMethods()) {
          if (Modifier.isStatic(method.getModifiers())) {
            continue;
          }

          if (isGetter(method) || isSetter(method)) {
            String propertyName = toPropertyName(method);
            PropertyDescriptor existing = findPropertyDescriptor(result, propertyName);

            if (isGetter(method) && existing == null) {
              result.add(new PropertyDescriptor(propertyName, method, null));
            } else if (isSetter(method) && existing != null) {
              existing.setWriteMethod(method);
            }
          }
        }
      }

      return result;
    } catch (IntrospectionException e) {
      throw new MappingException(String.format("Cannot introspect the type %s.", inspectType.getName()));
    }
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

  private static boolean hasGetter(PropertyDescriptor pd) {
    return pd.getReadMethod() != null;
  }

  private static boolean hasSetter(PropertyDescriptor pd) {
    return pd.getWriteMethod() != null;
  }

}
