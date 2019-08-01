package com.remondis.remap.collections.nestedCollections;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.basic.B;
import com.remondis.remap.basic.BResource;

public class NestedCollectionsTest {
  @Test
  public void shouldMapNestedCollections() {

    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .mapper();
    Mapper<A, AResource> aMapper = Mapping.from(A.class)
        .to(AResource.class)
        .useMapper(bMapper)
        .mapper();

    String[] stringsArr = new String[] {
        "A", "B", "C", "D"
    };

    String b1String = "b1String";
    int b1Number = 101;
    Integer b1Integer = 201;
    B b1 = new B(b1String, b1Number, b1Integer);

    String b2String = "b2String";
    int b2Number = 331;
    Integer b2Integer = 441;
    B b2 = new B(b2String, b2Number, b2Integer);

    A a = new A();

    Set<B> firstList = new HashSet<>(Arrays.asList(b1));
    Set<B> secondList = new HashSet<>(Arrays.asList(b2));
    a.addNestedLists(firstList, secondList);

    AResource ar = aMapper.map(a);

    // Assert before mapping (paranoia check)
    List<Set<B>> nestedListsBefore = a.getNestedLists();
    assertThat(nestedListsBefore).isInstanceOf(List.class);
    assertThat(nestedListsBefore.iterator()
        .next()).isInstanceOf(Set.class);

    // Assert after mapping (collections should be nested with according to the destination types)
    Set<List<BResource>> nestedLists = ar.getNestedLists();
    assertThat(nestedLists).isInstanceOf(Set.class);
    assertThat(nestedLists.iterator()
        .next()).isInstanceOf(List.class);
  }

}
