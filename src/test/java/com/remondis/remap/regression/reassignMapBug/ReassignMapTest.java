package com.remondis.remap.regression.reassignMapBug;

import static org.junit.Assert.assertNotNull;

import java.util.LinkedHashMap;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class ReassignMapTest {

  @Test
  public void shouldReassignMapsWithoutException() {

    Mapper<MapOwner, MapOwnerTarget> mapper = Mapping.from(MapOwner.class)
        .to(MapOwnerTarget.class)
        .reassign(MapOwner::getMap)
        .to(MapOwnerTarget::getAnotherMap)
        .mapper();

    LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
    map.put("key", "value");
    map.put("key1", "value1");

    MapOwner mapOwner = new MapOwner(map);
    MapOwnerTarget mapTarget = mapper.map(mapOwner);

    assertNotNull(mapTarget);

  }
}
