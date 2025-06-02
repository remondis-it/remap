package com.remondis.remap.regression.npeDenyAlreadyOmitted;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapping;

public class MapperTest {

  @Test
  public void shouldMap() {
    Mapping.from(A.class)
        .to(AResource.class)
        .omitInDestination(AResource::getOmitted)
        .replace(A::getMultiMapped, AResource::getMultiMapped1)
        .withSkipWhenNull(Function.identity())
        .replace(A::getMultiMapped, AResource::getMultiMapped2)
        .withSkipWhenNull(Function.identity())
        .replace(A::getMultiMapped, AResource::getMultiMapped3)
        .withSkipWhenNull(Function.identity())
        .mapper();
  }
}
