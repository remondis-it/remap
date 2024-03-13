package com.remondis.remap;

import static com.remondis.remap.ReflectionUtil.defaultValue;
import static com.remondis.remap.ReflectionUtil.hasReturnType;
import static com.remondis.remap.ReflectionUtil.invokeMethodProxySafe;
import static com.remondis.remap.ReflectionUtil.isGetter;
import static com.remondis.remap.ReflectionUtil.toPropertyName;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

public class InterceptionHandler<T> {

  Class<?> type;

  T proxyObject;

  ThreadLocal<List<String>> threadLocalPropertyNames = new ThreadLocal<>();

  /**
   * Resets the thread local list of property names.
   */
  void reset() {
    threadLocalPropertyNames.remove();
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
      List<String> propertyNames = threadLocalPropertyNames.get();
      // Lazy list creation.
      if (isNull(propertyNames)) {
        propertyNames = new LinkedList<>();
        threadLocalPropertyNames.set(propertyNames);
      }
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

  public void setProxyObject(T proxyObject) {
    this.proxyObject = proxyObject;
  }

  public T getProxyObject() {
    return proxyObject;
  }

  public List<String> getTrackedPropertyNames() {
    List<String> list = threadLocalPropertyNames.get();
    // Reset thread local after access.
    reset();
    // If list was never created (lazy-init)
    if (isNull(list)) {
      return Collections.emptyList();
    } else {
      return unmodifiableList(list);
    }
  }

  public boolean hasTrackedProperties() {
    return nonNull(threadLocalPropertyNames.get());
  }

  private static void denyNoReturnType(Method method) {
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
