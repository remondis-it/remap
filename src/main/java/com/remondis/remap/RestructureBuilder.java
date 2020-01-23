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
    return applying(conf -> conf.omitOtherSourceProperties());
  }

  public MappingConfiguration<S, D> applying(Consumer<MappingConfiguration<S, RD>> restructureMappingConfiguration) {
    denyNull("restructureMappingConfiguration", restructureMappingConfiguration);

    Map<Projection<?, ?>, InternalMapper<?, ?>> mappers = mappingConfiguration.getMappers();
    MappingConfiguration<S, RD> config = Mapping.from((Class<S>) mappingConfiguration.getSource())
        .to((Class<RD>) typedPropertyDescriptor.property.getPropertyType());
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
        typedPropertyDescriptor.property, null);
    mappingConfiguration.addDestinationMapping(typedPropertyDescriptor.property, restructureTransformation);
    return mappingConfiguration;
  }

}
