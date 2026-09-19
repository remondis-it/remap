package com.remondis.remap;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

/**
 * Reads the {@link Nullness} of the type usages of a Java Bean property from the JSpecify annotations of its accessor
 * methods.
 *
 * <p>
 * The JSpecify annotations are type-use annotations with runtime retention, so the nullness of a property is read from
 * the annotated return type of its getter and from the annotated parameter type of its setter. Following the JSpecify
 * specification, an unannotated type usage is only considered to be non-null if it is covered by a
 * {@link NullMarked} scope. The nearest scope wins, so a {@link NullUnmarked} method opts out of a null-marked class
 * and a null-marked class opts in although its package is not null-marked.
 * </p>
 *
 * <p>
 * Note that the nullness of a property is taken from its accessor methods and never from the backing field: the type
 * usage that ReMap reads from and writes to is the one declared by the getter and the setter. Code generators that do
 * not propagate the annotations of a field to its accessors therefore hide the nullness of a property from this
 * evaluation.
 * </p>
 */
class JSpecifyNullness {

  private JSpecifyNullness() {
  }

  /**
   * Returns the {@link Nullness} of the value a getter returns.
   *
   * @param getter The read method of a property.
   * @return Returns the {@link Nullness} of the return type.
   */
  static Nullness ofReturnType(Method getter) {
    return of(getter.getAnnotatedReturnType(), getter);
  }

  /**
   * Returns the {@link Nullness} of the value a setter accepts.
   *
   * @param setter The write method of a property.
   * @return Returns the {@link Nullness} of the first parameter type.
   */
  static Nullness ofParameter(Method setter) {
    AnnotatedType[] parameterTypes = setter.getAnnotatedParameterTypes();
    if (parameterTypes.length == 0) {
      return Nullness.UNSPECIFIED;
    }
    return of(parameterTypes[0], setter);
  }

  /**
   * Returns the {@link Nullness} of a type argument of the return type of the specified getter. This is used to
   * determine the nullness of collection elements as well as of map keys and values.
   *
   * @param getter The read method of a property.
   * @param index The index of the type argument.
   * @return Returns the {@link Nullness} of the type argument or {@link Nullness#UNSPECIFIED} if the return type is
   *         not parameterized or does not declare a type argument at the specified index.
   */
  static Nullness ofTypeArgument(Method getter, int index) {
    AnnotatedType returnType = getter.getAnnotatedReturnType();
    if (!(returnType instanceof AnnotatedParameterizedType)) {
      return Nullness.UNSPECIFIED;
    }
    AnnotatedType[] typeArguments = ((AnnotatedParameterizedType) returnType).getAnnotatedActualTypeArguments();
    if (index >= typeArguments.length) {
      return Nullness.UNSPECIFIED;
    }
    return of(typeArguments[index], getter);
  }

  /**
   * Returns the raw type of a type argument of the return type of the specified getter.
   *
   * @param getter The read method of a property.
   * @param index The index of the type argument.
   * @return Returns the raw type or <code>null</code> if the type argument is not a class, for example a type variable
   *         or a wildcard.
   */
  static @Nullable Class<?> typeArgumentOf(Method getter, int index) {
    AnnotatedType returnType = getter.getAnnotatedReturnType();
    if (!(returnType instanceof AnnotatedParameterizedType)) {
      return null;
    }
    AnnotatedType[] typeArguments = ((AnnotatedParameterizedType) returnType).getAnnotatedActualTypeArguments();
    if (index >= typeArguments.length) {
      return null;
    }
    Type type = typeArguments[index].getType();
    if (type instanceof Class) {
      return (Class<?>) type;
    }
    return null;
  }

  private static Nullness of(AnnotatedType annotatedType, Method declaringMethod) {
    if (annotatedType.isAnnotationPresent(Nullable.class)) {
      return Nullness.NULLABLE;
    }
    if (annotatedType.isAnnotationPresent(NonNull.class)) {
      return Nullness.NON_NULL;
    }
    Type type = annotatedType.getType();
    if (type instanceof Class && ((Class<?>) type).isPrimitive()) {
      return Nullness.NON_NULL;
    }
    if (!(type instanceof Class) && !(type instanceof ParameterizedType)) {
      // Type variables and wildcards carry the nullness of the type they are instantiated with, which is not known
      // here.
      return Nullness.UNSPECIFIED;
    }
    if (isNullMarked(declaringMethod)) {
      return Nullness.NON_NULL;
    }
    return Nullness.UNSPECIFIED;
  }

  /**
   * Determines whether the specified method is covered by a {@link NullMarked} scope. The nearest scope wins, so the
   * method is inspected first, then its declaring class and the enclosing classes, then the package and finally the
   * module.
   */
  private static boolean isNullMarked(Method method) {
    if (method.isAnnotationPresent(NullUnmarked.class)) {
      return false;
    }
    if (method.isAnnotationPresent(NullMarked.class)) {
      return true;
    }
    Class<?> declaringClass = method.getDeclaringClass();
    for (Class<?> type = declaringClass; type != null; type = type.getEnclosingClass()) {
      if (type.isAnnotationPresent(NullUnmarked.class)) {
        return false;
      }
      if (type.isAnnotationPresent(NullMarked.class)) {
        return true;
      }
    }
    Package declaringPackage = declaringClass.getPackage();
    if (declaringPackage != null) {
      if (declaringPackage.isAnnotationPresent(NullUnmarked.class)) {
        return false;
      }
      if (declaringPackage.isAnnotationPresent(NullMarked.class)) {
        return true;
      }
    }
    Module module = declaringClass.getModule();
    return module.isAnnotationPresent(NullMarked.class);
  }

}
