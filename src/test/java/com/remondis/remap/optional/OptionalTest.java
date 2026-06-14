package com.remondis.remap.optional;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.TypeMapping;

public class OptionalTest {

  @Test
  public void shouldNotMapNull() {

  }

  @Test
  public void shouldEmptyToNull() {

  }

  @Test
  public void shouldMapTypeToOptionalOptional() {

  }

  @Test
  public void shouldMapOptionalToType() {
    Mapper<A, B> mapper = Mapping.from(A.class)
        .to(B.class)
        .useMapper(TypeMapping.from(String.class)
            .to(Integer.class)
            .applying(String::length))
        .reassign(A::getOptOptString)
        .to(B::getOptOptInteger)
        .reassign(A::getOptOptString)
        .to(B::getOptInteger)
        .reassign(A::getOptOptString)
        .to(B::getInteger)

        .reassign(A::getOptOptString)
        .to(B::getOptOptString)
        .reassign(A::getOptOptString)
        .to(B::getOptString)
        .reassign(A::getOptOptString)
        .to(B::getString)

        .reassign(A::getOptListString)
        .to(B::getIntegers)
        .mapper();

    A a = new A("DEine MUDDER");
    B b = mapper.map(a);

    // TODO: Asserts.

  }

}
