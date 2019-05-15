package com.remondis.remap;

import static com.remondis.remap.ReflectionUtil.defaultValue;
import static com.remondis.remap.ReflectionUtil.hasReturnType;
import static com.remondis.remap.ReflectionUtil.invokeMethodProxySafe;
import static com.remondis.remap.ReflectionUtil.isGetter;
import static com.remondis.remap.ReflectionUtil.toPropertyName;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.InvocationHandler;

/**
 * The {@link InvocationSensor} tracks get-method invocations on a proxy class and makes the invocation information
 * available to the {@link Mapper}.
 *
 * @author schuettec
 */
class InvocationSensor<T> {

  private T proxyObject;

  private List<String> propertyNames = new LinkedList<>();

  InvocationSensor(Class<T> superType) {
    Enhancer enhancer = new Enhancer();
    enhancer.setSuperclass(superType);
    enhancer.setCallback(new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
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

    });
    proxyObject = superType.cast(enhancer.create());
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
