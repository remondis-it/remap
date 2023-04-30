package com.remondis.remap.implicitMappings.customTypeConversions;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;
import com.remondis.remap.TypeMapping;
import org.junit.jupiter.api.Test;

/**
 * A mapping of property B->B' is performed implicitly if the field names are equal and [ the type is equal OR a type
 * mapper was registered that maps type(b) -> type(b') ].
 */
class CustomTypeConversionsTest {

  @Test
  void errorHandling() {
    assertThatThrownBy(() -> Mapping.from(A.class)
        .to(AResource.class)
        .mapper()).isInstanceOf(MappingException.class)
        .hasMessage("No mapper found for type mapping from java.lang.CharSequence to java.lang.String.\n"
            + "For example used by the property mapping from Property 'addresses' in A to Property 'addresses' in AResource.");
  }

  @Test
  void mappingImplicitNullValues() {
    A a = new A(null);
    AResource aResource = mapper().map(a);
    assertNull(aResource.getAddresses());
  }

  @Test
  void mappingImplicit() {
    List<CharSequence> charSeqs = asList("Address 1", "Address 2");
    A a = new A(charSeqs);
    AResource aResource = mapper().map(a);
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
