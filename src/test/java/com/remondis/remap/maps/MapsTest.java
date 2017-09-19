package com.remondis.remap.maps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.junit.Test;

import com.remondis.remap.B;
import com.remondis.remap.BResource;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;

public class MapsTest {

  /**
   * Since maps are not supported out-of-the-box, the mapper should deny automatic
   * mapping of maps. A workaround should be to add a replace-operator.
   */
  @Test(expected = MappingException.class)
  public void shouldDenyImplicitMappingOfMaps() {
    Mapper<B, BResource> bMapper = Mapping.from(B.class)
      .to(BResource.class)
      .mapper();
    Mapping.from(A.class)
      .to(AResource.class)
      .useMapper(bMapper)
      .mapper();
  }

  /**
   * Since maps are not supported out-of-the-box, the mapper should support
   * mapping transformation via
   * {@link Mapping#replace(com.remondis.remap.TypedSelector, com.remondis.remap.TypedSelector)}.
   */
  @Test
  public void shouldWorkaroundMappingOfMaps() {
    Mapper<B, BResource> bMapper = Mapping.from(B.class)
      .to(BResource.class)
      .mapper();
    Mapper<A, AResource> mapper = Mapping.from(A.class)
      .to(AResource.class)
      .replace(A::getBmap, AResource::getBmap)
      .with(iToBMap -> {
        return iToBMap.entrySet()
          .stream()
          .map(e -> {
            return new AbstractMap.SimpleEntry<String, BResource>(String.valueOf(e.getKey()),
                                                                  bMapper.map(e.getValue()));
          })
          .collect(Collectors.toMap(Entry::getKey, Entry::getValue));

      })
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
