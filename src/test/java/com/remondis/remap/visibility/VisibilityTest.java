package com.remondis.remap.visibility;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class VisibilityTest {
  @Test
  void testVisibility() {
    Mapper<C, CResource> mapper = Mapping.from(C.class)
        .to(CResource.class)
        .mapper();

    String string = "A string";
    CResource cr = mapper.map(new C(string));
    assertEquals(string, cr.getString());
  }
}
