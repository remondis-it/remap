package com.remondis.remap.maps;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.TypeMapping;
import com.remondis.remap.basic.B;
import com.remondis.remap.basic.BResource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapsTest {

  @Test
  void shouldMapMapsUsingMappers() {
    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .mapper();
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .useMapper(TypeMapping.from(Integer.class)
            .to(String.class)
            .applying(String::valueOf))
        .useMapper(bMapper)
        .mapper();

    B b1 = new B("String1", 1, 101);
    B b2 = new B("String2", 2, 102);
    B b3 = new B("String3", 3, 103);

    BResource expectedBmapped1 = new BResource("String1", 1, 101);
    BResource expectedBmapped2 = new BResource("String2", 2, 102);
    BResource expectedBmapped3 = new BResource("String3", 3, 103);

    A a = new A();
    a.addB(1, b1);
    a.addB(2, b2);
    a.addB(3, b3);

    AResource aMapped = mapper.map(a);
    Map<String, BResource> bmap = aMapped.getBmap();
    assertEquals(3, bmap.size());

    assertEquals(expectedBmapped1, bmap.get("1"));
    assertEquals(expectedBmapped2, bmap.get("2"));
    assertEquals(expectedBmapped3, bmap.get("3"));

  }

  @Test
  void shouldWorkaroundMappingOfMaps() {
    Mapper<B, BResource> bMapper = Mapping.from(B.class)
        .to(BResource.class)
        .mapper();
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .replace(A::getBmap, AResource::getBmap)
        .with(iToBMap -> iToBMap.entrySet()
            .stream()
            .map(e -> new AbstractMap.SimpleEntry<>(String.valueOf(e.getKey()), bMapper.map(e.getValue())))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue)))
        .useMapper(bMapper)
        .mapper();

    String b1String = "b1String";
    int b1Number = 101;
    Integer b1Integer = 201;
    B b1 = new B(b1String, b1Number, b1Integer);

    String b2String = "b2String";
    int b2Number = 331;
    Integer b2Integer = 441;
    B b2 = new B(b2String, b2Number, b2Integer);

    A a = new A();
    a.addB(1, b1)
        .addB(2, b2);
    AResource map = mapper.map(a);

    Map<String, BResource> bmap = map.getBmap();

    assertTrue(bmap.containsKey("1"));
    assertTrue(bmap.containsKey("2"));

    BResource br1Actual = bmap.get("1");
    BResource br2Actual = bmap.get("2");

    assertEquals(b1String, b1.getString());
    assertEquals(b1String, br1Actual.getString());
    assertEquals(b1Number, b1.getNumber());
    assertEquals(b1Number, br1Actual.getNumber());
    assertEquals(b1Integer, b1.getInteger());
    assertEquals(b1Integer, br1Actual.getInteger());

    assertEquals(b2String, b2.getString());
    assertEquals(b2String, br2Actual.getString());
    assertEquals(b2Number, b2.getNumber());
    assertEquals(b2Number, br2Actual.getNumber());
    assertEquals(b2Integer, b2.getInteger());
    assertEquals(b2Integer, br2Actual.getInteger());

  }

}
