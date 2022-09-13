package com.remondis.remap;

import static com.remondis.remap.ReflectionUtil.*;
import static java.lang.ClassLoader.getSystemClassLoader;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

/**
 * The {@link InvocationSensor} tracks get-method invocations on a proxy class and makes the invocation information
 * available to the {@link Mapper}.
 *
 * @author schuettec
 */
public class InvocationSensor<T> {

  private T proxyObject;

  private List<String> propertyNames = new LinkedList<>();

  /**
   * Creates a proxy for the given class type.
   *
   * @param superType the class type for which the proxy should be created
   */
  public InvocationSensor(Class<T> superType) {
    T po = null;
    try {
      po = (T) new ByteBuddy().subclass(superType)
          .method(isDeclaredBy(superType))
          .intercept(MethodDelegation.to(this))
          .make()
          .load(getSystemClassLoader(), ClassLoadingStrategy.Default.INJECTION)
          .getLoaded()
          .getDeclaredConstructor()
          .newInstance();
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
      throw new MappingException(
          String.format("Error while creating proxy for class '%s'", superType.getCanonicalName()), ex);
    }
    proxyObject = po;
  }

  /**
   * This method will be called each time when the object proxy calls any of its methods.
   * 
   * @param method the intercepted method
   * @param args the given arguments ti the intercepted method
   * @throws Exception throws
   */
  @RuntimeType
  public Object intercept(@Origin Method method, @AllArguments Object[] args) throws Exception {
    if (isGetter(method)) {
      denyNoReturnType(method);
      // schuettec - Get property name from method and mark this property as called.
      String propertyName = toPropertyName(method);
      propertyNames.add(propertyName);
      // schuettec - Then return an appropriate default value.
      return nullOrDefaultValue(method.getReturnType());
    } else if (isObjectMethod(method)) {
      // schuettec - 08.02.2017 : Methods like toString, equals or hashcode are redirected to this invocation
      // handler.
      return invokeMethodProxySafe(method, this, args);
    } else {
      return nullOrDefaultValue(method.getReturnType());
    }
  }

  /**
   * Returns the proxy object get-method calls can be performed on.
   *
   * @return The proxy.
   */
  T getSensor() {
    return proxyObject;
  }

  /**
   * Returns the list of property names that were tracked by get calls.
   *
   * @return Returns the tracked property names.
   */
  List<String> getTrackedPropertyNames() {
    return Collections.unmodifiableList(propertyNames);
  }

  /**
   * Checks if there were any properties accessed by get calls.
   *
   * @return Returns <code>true</code> if there were at least one interaction with a property. Otherwise
   *         <code>false</code> is returned.
   */
  boolean hasTrackedProperties() {
    return !propertyNames.isEmpty();
  }

  /**
   * Resets all tracked information.
   */
  void reset() {
    propertyNames.clear();
  }

  private void denyNoReturnType(Method method) {
    if (!hasReturnType(method)) {
      throw MappingException.noReturnTypeOnGetter(method);
    }
  }

  private static Object nullOrDefaultValue(Class<?> returnType) {
    if (returnType.isPrimitive()) {
      return defaultValue(returnType);
    } else {
      return null;
    }
  }

  private static boolean isObjectMethod(Method method) {
    return method.getDeclaringClass() == Object.class;
  }

}
