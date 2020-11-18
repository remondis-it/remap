package com.remondis.remap.defaultMethods;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class DefaultMethodTest {
  @Test
  public void test_defaults_methods() {
    Mapper<Interface, DestinationBean> mapper = Mapping.from(Interface.class)
        .to(DestinationBean.class)
        .mapper();

    Interface source = new SourceBeanWithDefaults();
    DestinationBean destinationBean = mapper.map(source);
    assertEquals(destinationBean.getString(), source.getString());
  }

  @Test
  public void test_withOverridden_defaults_methods() {
    Mapper<Interface, DestinationBean> mapper = Mapping.from(Interface.class)
        .to(DestinationBean.class)
        .mapper();

    Interface source = new SourceBean("string");
    DestinationBean destinationBean = mapper.map(source);
    assertEquals(destinationBean.getString(), source.getString());
  }

}
