package com.remondis.remap.regression.readOnlyInterfacePropertyBug;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

/**
 * Reproduces <a href="https://github.com/remondis-it/remap/issues/175">issue #175</a>: If the mapping target
 * implements an interface that declares a getter without a corresponding setter, the mapper reported this
 * read-only property as a missing mapping although read-only properties are no mapping targets.
 */
class MapperTest {

  @Test
  void shouldCreateMapperIfTargetImplementsInterfaceWithReadOnlyProperty() {
    Mapper<C, A> mapper = Mapping.from(C.class)
        .to(A.class)
        .mapper();

    C source = new C();
    source.setId(42L);

    A target = mapper.map(source);

    assertThat(target.getId()).isEqualTo(42L);
    assertThat(target.getName()).isEqualTo("Test");
  }
}
