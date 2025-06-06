package com.remondis.remap.interfaceMapping;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.remondis.remap.AssertMapping;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class EntityMappingTest {

  private Mapper<EntityA, EntityADTO> entityMapperWithoutId;
  private Mapper<EntityA, EntityWithIdDTO> entityMapperWithId;

  @BeforeEach
  public void setup() {
    this.entityMapperWithoutId = Mapping.from(EntityA.class)
        .to(EntityADTO.class)
        .omitInSource(HasId::getId)
        .mapper();

    this.entityMapperWithId = Mapping.from(EntityA.class)
        .to(EntityWithIdDTO.class)
        .mapper();
  }

  @Test
  public void shouldMapEntityWithoutInterfaceAttribute() {
    EntityA entity = new EntityA();
    entity.setName("Test Entity");
    EntityADTO dto = entityMapperWithoutId.map(entity);

    assertEquals("Test Entity", dto.getName());
  }

  @Test
  public void shouldMapEntityWithInterfaceAttribute() {
    EntityA entity = new EntityA();
    entity.setName("Test Entity");
    EntityWithIdDTO dto = entityMapperWithId.map(entity);

    assertEquals("Test Entity", dto.getName());
    assertEquals(Long.valueOf(1), dto.getId());
  }

  @Test
  public void shouldComplainAboutImplicitMapping() {
    Mapper<EntityA, EntityADTO> implicitMapper = Mapping.from(EntityA.class)
        .to(EntityADTO.class)
        .omitInSource(EntityA::getId)
        .mapper();

    assertThatThrownBy(() -> AssertMapping.of(implicitMapper)
        .expectNoImplicitMappings()
        .ensure()).isInstanceOf(AssertionError.class)
            .hasMessageContaining("The mapper was expected to create no implicit mappings");
  }
}