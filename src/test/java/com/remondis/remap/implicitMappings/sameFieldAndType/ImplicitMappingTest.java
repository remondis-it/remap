package com.remondis.remap.implicitMappings.sameFieldAndType;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

/**
 * A mapping of property B->B' is performed implicitly if the field names are equal and [ the type is equal OR a type
 * mapper was registered that maps type(b) -> type(b') ].
 */
public class ImplicitMappingTest {
  private Mapper<A, AResource> aMapper;

  @Before
  public void setup() {
    this.aMapper = Mapping.from(A.class)
        .to(AResource.class)
        .mapper();
  }

  @Test
  public void mappingImplicit() {
    AResource aResource = aMapper
        .map(new A(new B("string"), asList(new B("string"), new B("string1"), new B("string2"))));
    assertNotNull(aResource.getB());
    List<B> bs = aResource.getBs();
    assertNotNull(bs);
    assertEquals(3, bs.size());
  }

}
