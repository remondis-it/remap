package com.remondis.remap.regression.methodCallsInConstructor;

import java.util.function.Function;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import org.junit.jupiter.api.Test;

class MethodCallsInConstructorTest {

  @Test
  void shouldNotComplainAboutInternalMethodsInConstructor() {
    Mapper<A, A> mapper = Mapping.from(A.class)
        .to(A.class)
        .replace(A::getaString, A::getaString)
        .with(Function.identity())
        .mapper();

    mapper.map(new A("AString"));
  }

}
