package com.remondis.remap.regression.noBeanCopyBug;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class NoBeanCopyBug {

  /**
   * There was a bug in mapping an object holding a {@link BigDecimal}, a Java object not complying to Java bean
   * convention. The attempt was made to copy this instance by calling a default constructor, but there is none.
   */
  @Test
  public void noBeanCopyBug() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .mapper();

    BigDecimal bigDecimal = new BigDecimal(1L);
    A a = new A(bigDecimal);
    AResource ar = mapper.map(a);

    assertEquals(bigDecimal, a.getBigDecimal());
    assertEquals(bigDecimal, ar.getBigDecimal());

  }

}
