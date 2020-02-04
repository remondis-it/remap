package com.remondis.remap.regression.methodCallsInConstructor;

import java.util.function.Function;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MethodCallsInConstructorTest {

  @Test
  public void shouldNotComplainAboutInternalMethodsInConstructor() {
    Mapper<A, A> mapper = Mapping.from(A.class)
        .to(A.class)
        .replace(A::getaString, A::getaString)
        .with(Function.identity())
        .mapper();

    mapper.map(new A("AString"));

  }

}
