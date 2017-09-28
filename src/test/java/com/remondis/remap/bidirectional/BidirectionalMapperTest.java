package com.remondis.remap.bidirectional;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.remondis.remap.BidirectionalMapper;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class BidirectionalMapperTest {

  @Test
  public void shouldMapViceVersa() {

    Mapper<A, B> to = Mapping.from(A.class)
        .to(B.class)
        .replace(A::getNumber, B::getString)
        .withSkipWhenNull(String::valueOf)
        .mapper();

    Mapper<B, A> from = Mapping.from(B.class)
        .to(A.class)
        .replace(B::getString, A::getNumber)
        .withSkipWhenNull(Long::parseLong)
        .mapper();

    BidirectionalMapper<A, B> abMapping = BidirectionalMapper.of(to, from);

    A a = new A(101L);
    B b = abMapping.map(a);
    A aFrom = abMapping.mapFrom(b);

    assertEquals((long) a.getNumber(), Long.parseLong(b.getString()));
    assertEquals(a, aFrom);
  }

}
