
package com.remondis.remap.regression.replaceOnCollectionBug;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReplaceOnCollectionsTest {

  private static final String FLAT_MODIFIER = "_";
  private static final String MODIFIER = "_modified";

  @Test
  void test() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .replace(A::getStrings, AResource::getStrings)
        .withSkipWhenNull(list -> {
          List<String> newList = new LinkedList<>();
          for (String s : list) {
            newList.add(s + MODIFIER);
          }
          return newList;
        })
        .mapper();

    AssertMapping.of(mapper)
        .expectReplace(A::getStrings, AResource::getStrings)
        .andSkipWhenNull()
        .ensure();

    A a = new A(Arrays.asList("A", "B", "C"));

    AResource ar = mapper.map(a);

    for (int i = 0; i < a.getStrings()
        .size(); i++) {
      assertEquals(a.getStrings()
          .get(i) + MODIFIER, ar.getStrings()
              .get(i));
    }

  }

  @Test
  void test_map_list_to_strings() {
    Mapper<A, AFlat> mapper = Mapping.from(A.class)
        .to(AFlat.class)
        .replace(A::getStrings, AFlat::getString)
        .withSkipWhenNull(list -> {
          StringBuilder newString = new StringBuilder();
          for (String s : list) {
            newString.append(s)
                .append(FLAT_MODIFIER);
          }
          return newString.toString();
        })
        .mapper();

    AssertMapping.of(mapper)
        .expectReplace(A::getStrings, AFlat::getString)
        .andSkipWhenNull()
        .ensure();

    A a = new A(Arrays.asList("A", "B", "C"));
    AFlat aflat = mapper.map(a);
    assertEquals("A" + FLAT_MODIFIER + "B" + FLAT_MODIFIER + "C" + FLAT_MODIFIER, aflat.getString());

  }

}
