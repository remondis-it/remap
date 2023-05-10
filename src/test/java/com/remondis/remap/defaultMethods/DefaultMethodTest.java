package com.remondis.remap.defaultMethods;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultMethodTest {
  @Test
  void test_defaults_methods() {
    Mapper<Interface, DestinationBean> mapper = Mapping.from(Interface.class)
        .to(DestinationBean.class)
        .mapper();

    Interface source = new SourceBeanWithDefaults();
    DestinationBean destinationBean = mapper.map(source);
    assertEquals(destinationBean.getString(), source.getString());
  }

  @Test
  void test_withOverridden_defaults_methods() {
    Mapper<Interface, DestinationBean> mapper = Mapping.from(Interface.class)
        .to(DestinationBean.class)
        .mapper();

    Interface source = new SourceBean("string");
    DestinationBean destinationBean = mapper.map(source);
    assertEquals(destinationBean.getString(), source.getString());
  }

}
