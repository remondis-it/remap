package com.remondis.remap.multimapping.reassign;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class MapperTest {

  @Test
  public void shouldAllowMultipleReassigns() {

    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getString)
        .to(AResource::getString1)
        .reassign(A::getString)
        .to(AResource::getString2)
        .reassign(A::getString)
        .to(AResource::getString3)
        .mapper();
    AssertMapping.of(mapper)
        .expectReassign(A::getString)
        .to(AResource::getString1)
        .expectReassign(A::getString)
        .to(AResource::getString2)
        .expectReassign(A::getString)
        .to(AResource::getString3)
        .ensure();

    String string = "somestring";
    AResource ar = mapper.map(new A(string));
    assertEquals(string, ar.getString1());
    assertEquals(string, ar.getString2());
    assertEquals(string, ar.getString3());

  }

}
