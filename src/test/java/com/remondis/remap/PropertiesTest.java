package com.remondis.remap;

import java.beans.PropertyDescriptor;
import java.util.Optional;

import com.remondis.remap.fluent.FluentSetterDto;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static com.remondis.remap.Target.DESTINATION;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.MethodName.class)
class PropertiesTest {

  @Test
  void a() {
    Optional<PropertyDescriptor> pdFluentSetterNotThere = Properties
        .getProperties(FluentSetterDto.class, DESTINATION, false)
        .stream()
        .filter(pd -> pd.getName()
            .equals("b1"))
        .findFirst();
    assertFalse(pdFluentSetterNotThere.isPresent());
  }

  @Test
  void b() {
    // Changes the property descriptor persistently (vm-wide?).
    // Some cache is working here
    Optional<PropertyDescriptor> pdFluentSetter = Properties.getProperties(FluentSetterDto.class, DESTINATION, true)
        .stream()
        .filter(pd -> pd.getName()
            .equals("b1"))
        .findFirst();
    assertTrue(pdFluentSetter.isPresent());
  }

  @Test
  void c() {
    Optional<PropertyDescriptor> pdFluentSetter = Properties.getProperties(FluentSetterDto.class, DESTINATION, false)
        .stream()
        .filter(pd -> pd.getName()
            .equals("b1"))
        .findFirst();

    assertFalse(pdFluentSetter.isPresent());
  }

}
