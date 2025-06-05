package com.remondis.remap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.PropertyDescriptor;
import java.util.Optional;
import java.util.Set;
 
import org.junit.jupiter.api.Test;

import com.remondis.remap.fluent.FluentSetterDto;

//@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PropertiesTest {

  @Test
  public void a() {
    Optional<PropertyDescriptor> pdFluentSetterNotThere = getProperties(FluentSetterDto.class, DESTINATION, false)
        .stream()
        .filter(pd -> pd.getName()
            .equals("b1"))
        .findFirst();
    assertFalse(pdFluentSetterNotThere.isPresent());
  }

  @Test
  public void b() {
    // Changes the property descriptor persistently (vm-wide?).
    // Some cache is working here
    Optional<PropertyDescriptor> pdFluentSetter = getProperties(FluentSetterDto.class, DESTINATION, true).stream()
        .filter(pd -> pd.getName()
            .equals("b1"))
        .findFirst();
    assertTrue(pdFluentSetter.isPresent());

  }

  @Test
  public void c() {
    Optional<PropertyDescriptor> pdFluentSetter = getProperties(FluentSetterDto.class, DESTINATION, false).stream()
        .filter(pd -> pd.getName()
            .equals("b1"))
        .findFirst();

    assertFalse(pdFluentSetter.isPresent());
  }

  @Test
  public void extendedClassDoesNotImplementsInterface() {
    // given
    // when
    getProperties(InternalDummyB.class, DESTINATION, false); // fill cache with correct values
    getProperties(InternalDummyA.class, DESTINATION, false); // override with bad properties from interface
    Set<PropertyDescriptor> actual = getProperties(InternalDummyB.class, DESTINATION, false);
    // then
    assertEquals(InternalDummyB.class, actual.stream()
        .findFirst()
        .get()
        .getReadMethod()
        .getDeclaringClass());
    assertEquals(InternalDummyB.class, actual.stream()
        .findFirst()
        .get()
        .getWriteMethod()
        .getDeclaringClass());
  }

  @Test
  public void interfaceIsNotFullyMapable() {
    // given
    // when
    PropertyDescriptor actual = getProperties(InternalDummyA.class, DESTINATION, false).stream()
        .filter(pd -> pd.getName()
            .equals("a"))
        .findFirst()
        .get();
    // then
    assertNotNull(actual.getReadMethod());
    assertNotNull(actual.getWriteMethod());
    assertEquals(InternalDummyInterface.class, actual.getReadMethod()
        .getDeclaringClass());
    assertEquals(InternalDummyA.class, actual.getWriteMethod()
        .getDeclaringClass());
  }

  @Test
  public void interfaceUsesGenericType() {
    try {
      getProperties(InternalDummyC.class, DESTINATION, false);
    } catch (Exception e) {
      fail(e.getClass()
          .getSimpleName() + " was thrown");
    }
  }

  private class InternalDummyA extends InternalDummyB implements InternalDummyInterface {
    int a;

    @Override
    public int getA() {
      return a;
    }

    public void setA(int a) {
      this.a = a;
    }
  }

  private class InternalDummyB {
    int b;

    public int getB() {
      return b;
    }

    public void setB(int b) {
      this.b = b;
    }
  }

  private interface InternalDummyInterface {
    int getA();

    int getB();

    void setB(int b);
  }

  public interface InternalDummyInterface2<I> {
    I getC();
  }

  public class InternalDummyC implements InternalDummyInterface2<Integer> {
    private Integer c;

    public Integer getC() {
      return c;
    }

    public void setC(Integer c) {
      this.c = c;
    }
  }
}