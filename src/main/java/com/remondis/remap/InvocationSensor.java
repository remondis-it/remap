package com.remondis.remap;

import static java.lang.ClassLoader.getSystemClassLoader;
import static java.util.Objects.isNull;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * The {@link InvocationSensor} tracks get-method invocations on a proxy class and makes the invocation information
 * available to the {@link Mapper}.
 *
 * @author schuettec
 */
public class InvocationSensor<T> {

  static Map<Class<?>, InterceptionHandler<?>> interceptionHandlerCache = new ConcurrentHashMap<>();

  private InterceptionHandler<T> interceptionHandler;

  /**
   * Creates a proxy for the given class type.
   *
   * @param superType the class type for which the proxy should be created
   */
  public InvocationSensor(Class<T> superType) {
    ClassLoader classLoader;
    if (isNull(superType) || isNull(superType.getClassLoader())) {
      classLoader = getSystemClassLoader();
    } else {
      classLoader = superType.getClassLoader();
    }
    try {
      if (interceptionHandlerCache.containsKey(superType)) {
        this.interceptionHandler = (InterceptionHandler<T>) interceptionHandlerCache.get(superType);
      } else {
        InterceptionHandler<T> interceptionHandler = new InterceptionHandler<>();
        T po = null;
        po = new ByteBuddy().subclass(superType)
            .method(isDeclaredByClassHierarchy(superType))
            .intercept(MethodDelegation.to(interceptionHandler))
            .make()
            .load(classLoader, ClassLoadingStrategy.Default.INJECTION)
            .getLoaded()
            .getDeclaredConstructor()
            .newInstance();
        interceptionHandler.setProxyObject(po);
        interceptionHandlerCache.put(superType, interceptionHandler);
        this.interceptionHandler = interceptionHandler;
      }

    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
      throw new MappingException(
          String.format("Error while creating proxy for class '%s'", superType.getCanonicalName()), ex);
    }
  }

  /**
   * Creates an {@link ElementMatcher.Junction} for the method description of all superclasses, interfaces and the given
   * type itself so that all of those methods are proxied by the {@link InvocationSensor}.
   *
   * @param type type to get the junction for
   * @return the junction with all superclasses and interfaces including the given typeD
   */
  private ElementMatcher.Junction<MethodDescription> isDeclaredByClassHierarchy(Class<T> type) {
    ClassHierarchyIterator classHierarchyIterator = new ClassHierarchyIterator(type);
    ElementMatcher.Junction<MethodDescription> methodDescriptionJunction = null;
    while (classHierarchyIterator.hasNext()) {
      Class<?> next = classHierarchyIterator.next();
      if (isNull(methodDescriptionJunction)) {
        methodDescriptionJunction = isDeclaredBy(next);
      } else {
        methodDescriptionJunction = methodDescriptionJunction.or(isDeclaredBy(next));
      }
    }
    return methodDescriptionJunction;
  }

  /**
   * Returns the proxy object get-method calls can be performed on.
   *
   * @return The proxy.
   */
  T getSensor() {
    return interceptionHandler.getProxyObject();
  }

  /**
   * Returns the list of property names that were tracked by get calls.
   *
   * @return Returns the tracked property names.
   */
  List<String> getTrackedPropertyNames() {
    List<String> trackesPropertyNames = interceptionHandler.getTrackedPropertyNames();
    return trackesPropertyNames;
  }

  /**
   * Checks if there were any properties accessed by get calls.
   *
   * @return Returns <code>true</code> if there were at least one interaction with a property. Otherwise
   *         <code>false</code> is returned.
   */
  boolean hasTrackedProperties() {
    boolean hasTrackedProperties = interceptionHandler.hasTrackedProperties();
    return hasTrackedProperties;
  }

}
