package com.remondis.remap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyDescriptor;
import java.util.Optional;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import com.remondis.remap.fluent.FluentSetterDto;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PropertiesTest {

  @Test
  public void a() {
    Optional<PropertyDescriptor> pdFluentSetterNotThere = Properties
        .getProperties(FluentSetterDto.class, Target.DESTINATION, false)
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
    Optional<PropertyDescriptor> pdFluentSetter = Properties
        .getProperties(FluentSetterDto.class, Target.DESTINATION, true)
        .stream()
        .filter(pd -> pd.getName()
            .equals("b1"))
        .findFirst();
    assertTrue(pdFluentSetter.isPresent());

  }

  @Test
  public void c() {
    Optional<PropertyDescriptor> pdFluentSetter = Properties
        .getProperties(FluentSetterDto.class, Target.DESTINATION, false)
        .stream()
        .filter(pd -> pd.getName()
            .equals("b1"))
        .findFirst();

    assertFalse(pdFluentSetter.isPresent());
  }

}
