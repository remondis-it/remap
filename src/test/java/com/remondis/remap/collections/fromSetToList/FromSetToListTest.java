package com.remondis.remap.collections.fromSetToList;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import org.junit.jupiter.api.Test;

class FromSetToListTest {

  @Test
  void test() {
    A a = new A();
    a.add(new A1("a1"));
    a.add(new A1("a2"));
    a.add(new A1("a3"));

    Mapper<A, AMapped> mapper = Mapping.from(A.class)
        .to(AMapped.class)
        .useMapper(Mapping.from(A1.class)
            .to(A2.class)
            .mapper())
        .mapper();

    AMapped aMapped = mapper.map(a);
    assertThat(aMapped.getAs()).isInstanceOf(List.class);
  }
}
