package com.remondis.remap.maps.reassignMaps;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.TypeMapping;

public class MapperTest {

  @Test
  public void shouldReassingMapsWithTypeMapping() {
    Mapper<A, AResource> mapper = Mapping.from(A.class)
        .to(AResource.class)
        .useMapper(TypeMapping.from(Map.class)
            .to(Map.class)
            .applying(m -> new HashMap<>(m)))
        .mapper();

  }
}
