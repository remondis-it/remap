package com.remondis.remap.generics;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class GenericsTest {

  @Test
  public void shouldMapListOfGenericTypeFromInstance() {

    List<Bean<String>> list = asList(new Bean<>("A"), new Bean<>("B"), new Bean<>("C"));

    Mapper<Bean<String>, Bean2<String>> mapper = Mapping.from(new Bean<String>())
        .to(new Bean2<String>())
        .reassign(Bean::getObject)
        .to(Bean2::getReference)
        .mapper();

    List<Bean2<String>> mappedList = mapper.map(list);

    assertSame(list.get(0)
        .getObject(),
        mappedList.get(0)
            .getReference());
    assertSame(list.get(1)
        .getObject(),
        mappedList.get(1)
            .getReference());
    assertSame(list.get(2)
        .getObject(),
        mappedList.get(2)
            .getReference());

  }

  @Test
  public void shouldMapGenericTypeFromInstance() {
    Mapper<Bean<String>, Bean2<String>> mapper = Mapping.from(new Bean<String>())
        .to(new Bean2<String>())
        .reassign(Bean::getObject)
        .to(Bean2::getReference)
        .mapper();

    String string = "String";
    Bean<String> source = new Bean<>(string);
    Bean2<String> bean2 = mapper.map(source);

    assertSame(string, bean2.getReference());

  }

  @Test
  public void shouldMapGenericType() {
    Mapper<Bean, Bean2> mapper = Mapping.from(Bean.class)
        .to(Bean2.class)
        .reassign(Bean::getObject)
        .to(Bean2::getReference)
        .mapper();

    String string = "String";
    Bean<String> source = new Bean<>(string);
    Bean2 bean2 = mapper.map(source);

    assertSame(string, bean2.getReference());

  }

  @Test
  public void shouldMap() {
    Mapper<Identifiable, IdentifiableImpl> idMapper = Mapping.from(Identifiable.class)
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
