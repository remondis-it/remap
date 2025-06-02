package com.remondis.remap.visibility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class VisibilityTest {
  @Test
  public void testVisibility() {
    Mapper<C, CResource> mapper = Mapping.from(C.class)
        .to(CResource.class)
        .mapper();

    String string = "A string";
    CResource cr = mapper.map(new C(string));
    assertEquals(string, cr.getString());
  }
}
