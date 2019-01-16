package com.remondis.remap.regression.implicitPrimitiveToWrapperMappingBug;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MappingTest {

  @Test
  public void integer_implicitMappingFromPrimitiveToWrapper() {
    Mapper<IntPrimitive, IntWrapper> mapper = Mapping.from(IntPrimitive.class)
        .to(IntWrapper.class)
        .mapper();
    AssertMapping.of(mapper)
        .ensure();
  }

  @Test
  public void integer_MappingWrapperToPrimitive() {
    Mapper<IntWrapper, IntPrimitive> mapper = Mapping.from(IntWrapper.class)
        .to(IntPrimitive.class)
        .mapper();
    AssertMapping.of(mapper)
        .ensure();
  }

  @Test
  public void boolean_implicitMappingFromPrimitiveToWrapper() {
    Mapper<BoolPrimitive, BoolWrapper> mapper = Mapping.from(BoolPrimitive.class)
        .to(BoolWrapper.class)
        .mapper();
    AssertMapping.of(mapper)
        .ensure();
  }

  @Test
  public void boolean_implicitMappingWrapperToPrimitive() {
    Mapper<BoolWrapper, BoolPrimitive> mapper = Mapping.from(BoolWrapper.class)
        .to(BoolPrimitive.class)
        .mapper();
    AssertMapping.of(mapper)
        .ensure();
  }
}
