package com.remondis.remap.implicitMappings.sameFieldNames;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

/**
 * A mapping of property B->B' is performed implicitly if the field names are equal and [ the type is equal OR a type
 * mapper was registered that maps type(b) -> type(b') ].
 */
public class ImplicitMappingTest {

  @Test
  public void mappingImplicitNullValues() {
    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .reassign(B::getString)
        .to(BResource::getAnotherString)
        .mapper();
    Mapper<A, AResource> aMapper = Mapping.from(A.class)
        .to(AResource.class)
        .useMapper(bMapper)
        .mapper();

    AResource aResource = aMapper.map(new A(null, null));
    assertNull(aResource.getB());
  }

  @Test
  public void mappingImplicit() {
    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .reassign(B::getString)
        .to(BResource::getAnotherString)
        .mapper();
    Mapper<A, AResource> aMapper = Mapping.from(A.class)
        .to(AResource.class)
        .useMapper(bMapper)
        .mapper();

    AResource aResource = aMapper
        .map(new A(new B("string"), asList(new B("string"), new B("string1"), new B("string2"))));
    assertNotNull(aResource.getB());
    List<BResource> bs = aResource.getBs();
    assertNotNull(bs);
    assertEquals(3, bs.size());
  }

}
