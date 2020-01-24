package com.remondis.remap;

import java.util.Map;
import java.util.function.Consumer;

import static com.remondis.remap.Lang.denyNull;

public class RestructureBuilder<S, D, RD> {

  private MappingConfiguration<S, D> mappingConfiguration;
  private TypedPropertyDescriptor<RD> typedPropertyDescriptor;

  public RestructureBuilder(MappingConfiguration<S, D> mappingConfiguration,
      TypedPropertyDescriptor<RD> typedPropertyDescriptor) {
    this.mappingConfiguration = mappingConfiguration;
    this.typedPropertyDescriptor = typedPropertyDescriptor;
  }

  public MappingConfiguration<S, D> implicitly() {
    return createRestructure(conf -> {
    }, false);
  }

  public MappingConfiguration<S, D> applying(Consumer<MappingConfiguration<S, RD>> restructureMappingConfiguration) {
    denyNull("restructureMappingConfiguration", restructureMappingConfiguration);
    boolean applyingSpecificConfiguration = true;
    return createRestructure(restructureMappingConfiguration, applyingSpecificConfiguration);
  }

  private MappingConfiguration<S, D> createRestructure(
      Consumer<MappingConfiguration<S, RD>> restructureMappingConfiguration, boolean applyingSpecificConfiguration) {
    Map<Projection<?, ?>, InternalMapper<?, ?>> mappers = mappingConfiguration.getMappers();
    MappingConfiguration<S, RD> config = Mapping.from((Class<S>) mappingConfiguration.getSource())
        .to((Class<RD>) typedPropertyDescriptor.property.getPropertyType());
    // Do not make all source properties mandatory
    config.omitOtherSourceProperties();
    // Inherit registered mappers.
    mappers.entrySet()
        .stream()
        .forEach(entry -> {
          Class<?> destType = entry.getKey()
              .getDestination();
          InternalMapper<?, ?> mapper = entry.getValue();
          config.useInternalMapper(mapper);
        });
    restructureMappingConfiguration.accept(config);
    Transformation restructureTransformation = new RestructureTransformation<>(config, null,
        typedPropertyDescriptor.property, null, applyingSpecificConfiguration);
    mappingConfiguration.addDestinationMapping(typedPropertyDescriptor.property, restructureTransformation);
    return mappingConfiguration;
  }

}
