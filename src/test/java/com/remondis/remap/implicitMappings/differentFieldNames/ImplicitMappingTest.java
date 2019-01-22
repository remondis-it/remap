package com.remondis.remap.implicitMappings.differentFieldNames;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.remondis.remap.AssertMapping;
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
    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .reassign(B::getString)
        .to(BResource::getAnotherString)
        .mapper();
    this.aMapper = Mapping.from(A.class)
        .to(AResource.class)
        .reassign(A::getB)
        .to(AResource::getbResource)
        .reassign(A::getBs)
        .to(AResource::getbResources)
        .useMapper(bMapper)
        .mapper();
  }

  @Test
  public void testAssert() {
    AssertMapping.of(aMapper)
        .expectReassign(A::getB)
        .to(AResource::getbResource)
        .expectReassign(A::getBs)
        .to(AResource::getbResources)
        .ensure();
  }

  @Test
  public void mappingImplicitNullValues() {
    AResource aResource = aMapper.map(new A(null, null));
    assertNull(aResource.getbResource());
  }

  @Test
  public void mappingImplicit() {
    AResource aResource = aMapper
        .map(new A(new B("string"), asList(new B("string"), new B("string1"), new B("string2"))));
    assertNotNull(aResource.getbResource());
    List<BResource> bs = aResource.getbResources();
    assertNotNull(bs);
    assertEquals(3, bs.size());
  }

}
