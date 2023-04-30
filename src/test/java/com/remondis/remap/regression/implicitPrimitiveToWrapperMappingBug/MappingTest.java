package com.remondis.remap.regression.implicitPrimitiveToWrapperMappingBug;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

class MappingTest {

  @Test
  void integer_implicitMappingFromPrimitiveToWrapper() {
    Mapper<IntPrimitive, IntWrapper> mapper = Mapping.from(IntPrimitive.class)
        .to(IntWrapper.class)
        .mapper();

    IntPrimitive intPrim = new IntPrimitive(101);
    IntWrapper intWrap = mapper.map(intPrim);

    assertEquals(intPrim.getInteger(), (int) intWrap.getInteger());

    AssertMapping.of(mapper)
        .ensure();
  }

  @Test
  void integer_MappingWrapperToPrimitive() {
    Mapper<IntWrapper, IntPrimitive> mapper = Mapping.from(IntWrapper.class)
        .to(IntPrimitive.class)
        .mapper();

    IntWrapper intWrap = new IntWrapper(Integer.valueOf(101));
    IntPrimitive intPrim = mapper.map(intWrap);

    assertEquals((int) intWrap.getInteger(), intPrim.getInteger());

    AssertMapping.of(mapper)
        .ensure();
  }

  @Test
  void boolean_implicitMappingFromPrimitiveToWrapper() {
    Mapper<BoolPrimitive, BoolWrapper> mapper = Mapping.from(BoolPrimitive.class)
        .to(BoolWrapper.class)
        .mapper();

    BoolPrimitive prim = new BoolPrimitive(true);
    BoolWrapper wrap = mapper.map(prim);

    assertEquals(prim.isBool(), wrap.getBool());

    AssertMapping.of(mapper)
        .ensure();
  }

  @Test
  void boolean_implicitMappingWrapperToPrimitive() {
    Mapper<BoolWrapper, BoolPrimitive> mapper = Mapping.from(BoolWrapper.class)
        .to(BoolPrimitive.class)
        .mapper();

    BoolWrapper wrap = new BoolWrapper(true);
    BoolPrimitive prim = mapper.map(wrap);

    assertEquals(wrap.getBool(), prim.isBool());

    AssertMapping.of(mapper)
        .ensure();
  }
}
