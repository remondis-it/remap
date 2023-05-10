package com.remondis.remap.maps.reassign;

import java.util.Map;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReassignTest {

  @Test
  void shouldReassignMaps() {
    Mapper<A, AMapped> mapper = Mapping.from(A.class)
        .to(AMapped.class)
        .reassign(A::getBmap)
        .to(AMapped::getAnotherName)
        .mapper();

    A a = new A();
    B b1 = new B("1");
    B b2 = new B("2");
    a.addB(1, b1);
    a.addB(2, b2);
    AMapped aMapped = mapper.map(a);
    Map<Integer, B> map = aMapped.getAnotherName();
    assertEquals(map.get(1), b1);
    assertEquals(map.get(2), b2);

  }

}
