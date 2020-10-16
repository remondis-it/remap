package com.remondis.remap;

import static java.util.Objects.requireNonNull;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Stack;

/**
 * Holds generic parameter types when traversing recursively through nested generic type declarations.
 */
public class GenericParameterContext {

  private Stack<ParameterizedType> stack = new Stack<>();
  private boolean finished = false;
  private Class<?> currentType;

  private Method method;

  /**
   * Creates a new context.
   *
   * @param method For this method.
   */
  public GenericParameterContext(Method method) {
    super();
    this.method = method;
    init();
  }

  GenericParameterContext(Stack<ParameterizedType> stack, boolean finished, Class<?> currentType, Method method) {
    super();
    this.stack = stack;
    this.finished = finished;
    this.currentType = currentType;
    this.method = method;
  }

  private void init() {
    Type genericReturnType = method.getGenericReturnType();
    if (genericReturnType instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
      add(parameterizedType);
      this.currentType = (Class<?>) parameterizedType.getRawType();
    } else if (genericReturnType instanceof TypeVariable) {
      this.currentType = Object.class;
      finish();
    } else {
      this.currentType = (Class<?>) genericReturnType;
      finish();
    }
  }

  private boolean isEmpty() {
    return stack.isEmpty();
  }

  /**
   * Adds a new recursive level generic type traversal.
   *
   * @param parameterizedType The current parameterized type.
   */
  private void add(ParameterizedType parameterizedType) {
    if (isFinished()) {
      throw new IllegalStateException(
          "The generic parameter context was finished, the concrete type was found. Further use is not supported.");
    }
    requireNonNull(parameterizedType, "parameterizedType must not be null.");
    stack.push(parameterizedType);
  }

  private boolean isFinished() {
    return finished;
  }

  private void finish() {
    this.finished = true;
  }

  /**
   * Returns the current parameterized type.
   *
   * @return Returns the current parameterized type.
   */
  public ParameterizedType get() {
    return stack.peek();
  }

  public Class<?> getCurrentType() {
    return this.currentType;
  }

  /**
   * Traverses the next recursive level in the nested generic types.
   *
   * @param genericParameterIndex The generic parameter index. Example: Map&lt;A,B&gt;: A=0, B=1.
   * @return Return the type after traversing one step.
   */
  public GenericParameterContext goInto(int genericParameterIndex) {
    Stack<ParameterizedType> newStack = new Stack<>();
    newStack.addAll(stack);
    GenericParameterContext newCtx = new GenericParameterContext(newStack, finished, currentType, method);
    newCtx.findNextGenericTypeFromMethod(genericParameterIndex);
    return newCtx;
  }

  /**
   * Finds the generic return type of a method in nested generics. For example this method returns {@link String} when
   * called on a method like <code>List&lt;List&lt;Set&lt;String&gt;&gt;&gt; get();</code>.
   *
   * @param method The method to analyze.
   */
  void findNextGenericTypeFromMethod(int genericParameterIndex) {
    // If the first type is requested, initialize context.
    Type type = null;

    ParameterizedType parameterizedType = get();
    type = parameterizedType.getActualTypeArguments()[genericParameterIndex];

    if (type instanceof ParameterizedType) {
      parameterizedType = (ParameterizedType) type;
      add(parameterizedType);
      this.currentType = (Class<?>) parameterizedType.getRawType();
    } else {
      finish();
      this.currentType = (Class<?>) type;
    }
  }

  @Override
  public String toString() {
    return "GenericParameterContext [currentType=" + currentType + ", currentParameterizedType="
        + (isEmpty() ? "empty" : get()) + "]";
  }

}
