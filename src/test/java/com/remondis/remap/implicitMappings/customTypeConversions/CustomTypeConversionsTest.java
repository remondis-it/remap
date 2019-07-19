package com.remondis.remap.implicitMappings.customTypeConversions;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;
import com.remondis.remap.TypeMapping;

/**
 * A mapping of property B->B' is performed implicitly if the field names are equal and [ the type is equal OR a type
 * mapper was registered that maps type(b) -> type(b') ].
 */
public class CustomTypeConversionsTest {

  @Test
  public void errorHandling() {

    Mapping.from(A.class)
        .to(AResource.class)
        .mapper();

    assertThatThrownBy(() -> Mapping.from(A.class)
        .to(AResource.class)
        .mapper()).isInstanceOf(MappingException.class)
            .hasMessage(
                "No mapper found for type mapping from java.lang.CharSequence to java.lang.String.\nFor example used by the property mapping from Property 'forename' (java.lang.CharSequence) in A to Property 'forename' (java.lang.String) in AResource");
  }

  @Test
  public void mappingImplicitNullValues() {
    A a = new A(null, null);
    AResource aResource = mapper().map(a);
    assertNull(aResource.getForename());
    assertNull(aResource.getAddresses());
  }

  @Test
  public void mappingImplicit() {
    List<CharSequence> charSeqs = asList((CharSequence) "Address 1", (CharSequence) "Address 2");
    CharSequence charSeq = "Forename";
    A a = new A(charSeq, charSeqs);
    AResource aResource = mapper().map(a);
    assertNotNull(aResource.getForename());
    List<String> addresses = aResource.getAddresses();
    assertNotNull(addresses);
    assertEquals(2, addresses.size());
  }

  private Mapper<A, AResource> mapper() {
    return Mapping.from(A.class)
        .to(AResource.class)
        .useMapper(TypeMapping.from(CharSequence.class)
            .to(String.class)
            .applying(String::valueOf))
        .mapper();
  }

}
