package com.remondis.remap.regression.implicitPrimitiveToWrapperMappingBug;

import static org.junit.Assert.assertTrue;

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

    IntPrimitive intPrim = new IntPrimitive(101);
    IntWrapper intWrap = mapper.map(intPrim);

    assertTrue(intPrim.getInteger() == intWrap.getInteger());

    AssertMapping.of(mapper)
        .ensure();
  }

  @Test
  public void integer_MappingWrapperToPrimitive() {
    Mapper<IntWrapper, IntPrimitive> mapper = Mapping.from(IntWrapper.class)
        .to(IntPrimitive.class)
        .mapper();

    IntWrapper intWrap = new IntWrapper(Integer.valueOf(101));
    IntPrimitive intPrim = mapper.map(intWrap);

    assertTrue(intWrap.getInteger() == intPrim.getInteger());

    AssertMapping.of(mapper)
        .ensure();
  }

  @Test
  public void boolean_implicitMappingFromPrimitiveToWrapper() {
    Mapper<BoolPrimitive, BoolWrapper> mapper = Mapping.from(BoolPrimitive.class)
        .to(BoolWrapper.class)
        .mapper();

    BoolPrimitive prim = new BoolPrimitive(true);
    BoolWrapper wrap = mapper.map(prim);

    assertTrue(prim.isBool() == wrap.getBool());

    AssertMapping.of(mapper)
        .ensure();
  }

  @Test
  public void boolean_implicitMappingWrapperToPrimitive() {
    Mapper<BoolWrapper, BoolPrimitive> mapper = Mapping.from(BoolWrapper.class)
        .to(BoolPrimitive.class)
        .mapper();

    BoolWrapper wrap = new BoolWrapper(true);
    BoolPrimitive prim = mapper.map(wrap);

    assertTrue(wrap.getBool() == prim.isBool());

    AssertMapping.of(mapper)
        .ensure();
  }
}
