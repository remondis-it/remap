package com.remondis.remap.generics;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.remondis.remap.GenericTypeReference;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class GenericsTest {

  @Test
  public void shouldMap() {
    Mapper<Identifiable<Long>, IdentifiableImpl> idMapper = Mapping.from(new GenericTypeReference<Identifiable<Long>>() {})
        .to(IdentifiableImpl.class)
        .replace(Identifiable::getId, IdentifiableImpl::getId)
        .with((o) -> {
          if (o instanceof Long) {
            return (Long) o;
          } else {
            throw new UnsupportedOperationException("Cannot map object to Long.");
          }
        })
        .mapper();

    IdentifiableImpl id = new IdentifiableImpl(1L);
    IdentifiableImpl mapResult = idMapper.map(id);

    assertEquals(id.getId(), mapResult.getId());
  }
}
