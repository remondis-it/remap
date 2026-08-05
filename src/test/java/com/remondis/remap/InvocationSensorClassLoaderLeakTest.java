package com.remondis.remap;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

/**
 * Regression test for the class loader leak caused by the former static proxy cache of {@link InvocationSensor}: the
 * cache held strong references to every sensed type and its generated proxy, so the class loader of a mapped type
 * (e.g. a web application class loader) could never be garbage collected.
 */
class InvocationSensorClassLoaderLeakTest {

  private static final String BEAN_CLASS_NAME = "com.remondis.remap.LeakTestBean";

  @Test
  void shouldNotPreventClassLoaderGarbageCollection() throws Exception {
    URL testClasses = Paths.get("target", "test-classes")
        .toUri()
        .toURL();
    BeanFirstClassLoader loader = new BeanFirstClassLoader(new URL[] {
        testClasses
    }, getClass().getClassLoader());

    Class<?> foreignBean = loader.loadClass(BEAN_CLASS_NAME);
    assertThat(foreignBean.getClassLoader()).isSameAs(loader);

    InvocationSensor<?> invocationSensor = new InvocationSensor<>(foreignBean);
    assertThat(invocationSensor.getSensor()).isInstanceOf(foreignBean);

    WeakReference<ClassLoader> loaderReference = new WeakReference<>(loader);
    loader.close();
    loader = null;
    foreignBean = null;
    invocationSensor = null;

    for (int i = 0; i < 50 && loaderReference.get() != null; i++) {
      System.gc();
      Thread.sleep(50);
    }
    assertThat(loaderReference.get())
        .as("The InvocationSensor must not prevent garbage collection of the sensed type's class loader.")
        .isNull();
  }

  /**
   * Loads the leak test bean itself instead of delegating to the parent, so the bean class is owned by this
   * throwaway class loader. All other classes (ReMap, JDK) are delegated to the parent, which mirrors a container
   * setup where the mapped beans live in an application class loader while the library is provided by a parent.
   */
  private static class BeanFirstClassLoader extends URLClassLoader {

    BeanFirstClassLoader(URL[] urls, ClassLoader parent) {
      super(urls, parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
      if (BEAN_CLASS_NAME.equals(name)) {
        synchronized (getClassLoadingLock(name)) {
          Class<?> loaded = findLoadedClass(name);
          if (loaded == null) {
            loaded = findClass(name);
          }
          if (resolve) {
            resolveClass(loaded);
          }
          return loaded;
        }
      }
      return super.loadClass(name, resolve);
    }
  }
}
