package com.remondis.remap.fluent;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ChainedSetterTest {

  @Test
  public void testChainedSetter() {
    Mapper<A, A> m = Mapping.from(A.class)
        .to(A.class)
        .allowFluentSetters()
        .mapper();
    A expected = new A().setInteger(5)
        .setI(22)
        .setS("str")
        .setB1(true)
        .setB2(true);
    A actual = m.map(expected);
    assertThat(actual).isEqualToComparingFieldByField(expected);
  }

  @Test(expected = MappingException.class)
  public void checkConfigurationOfChainedSetters() {
    Mapping.from(A.class)
        .to(A.class)
        .mapper();
  }

}
