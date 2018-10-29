package com.remondis.remap.implicitMappings.customTypeConversions;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.TypeMapping;

/**
 * A mapping of property B->B' is performed implicitly if the field names are equal and [ the type is equal OR a type
 * mapper was registered that maps type(b) -> type(b') ].
 */
public class CustomTypeConversionsTest {

  private Mapper<A, AResource> aMapper;

  @Before
  public void setup() {
    this.aMapper = Mapping.from(A.class)
        .to(AResource.class)
        .useMapper(TypeMapping.from(CharSequence.class)
            .to(String.class)
            .applying(String::valueOf))
        .mapper();
  }

  @Test
  public void mappingImplicitNullValues() {
    A a = new A(null, null);
    AResource aResource = aMapper.map(a);
    assertNull(aResource.getForname());
    assertNull(aResource.getAddresses());
  }

  @Test
  public void mappingImplicit() {
    List<CharSequence> charSeqs = asList((CharSequence) "Address 1", (CharSequence) "Address 2");
    CharSequence charSeq = "Forename";
    A a = new A(charSeq, charSeqs);
    AResource aResource = aMapper.map(a);
    assertNotNull(aResource.getForname());
    List<String> addresses = aResource.getAddresses();
    assertNotNull(addresses);
    assertEquals(2, addresses.size());
  }

}
