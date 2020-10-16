package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.util.Map;
import java.util.function.Consumer;

/**
 * A builder for restructuring a field in the destination type.
 *
 * @param <S> The source type of mapping.
 * @param <D> The destination type of mapping.
 * @param <RD> The type of the destination field.
 *
 * @author schuettec
 *
 */
public class RestructureBuilder<S, D, RD> {

  private MappingConfiguration<S, D> mappingConfiguration;
  private TypedPropertyDescriptor<RD> typedPropertyDescriptor;

  RestructureBuilder(MappingConfiguration<S, D> mappingConfiguration,
      TypedPropertyDescriptor<RD> typedPropertyDescriptor) {
    this.mappingConfiguration = mappingConfiguration;
    this.typedPropertyDescriptor = typedPropertyDescriptor;
  }

  /**
   * Tells the mapping, that the destination object can be restructured by simple implicit mappings. Use this
   * method if no custom mappings are required to build the destination object.
   *
   * @return Returns a {@link MappingConfiguration} for further configuration.
   */
  public MappingConfiguration<S, D> implicitly() {
    return createRestructure(conf -> {
    }, false);
  }

  /**
   * Adds further mapping configurations to the mapping that is used to restructure the destination object. Use this
   * method, if custom mappings are required to build the destination object.
   *
   * @param restructureMappingConfiguration A {@link Consumer} that receives a {@link MappingConfiguration} and applies
   *        further mapping configurations.
   * @return Returns a {@link MappingConfiguration} for further configuration.
   */
  public MappingConfiguration<S, D> applying(Consumer<MappingConfiguration<S, RD>> restructureMappingConfiguration) {
    denyNull("restructureMappingConfiguration", restructureMappingConfiguration);
    boolean applyingSpecificConfiguration = true;
    return createRestructure(restructureMappingConfiguration, applyingSpecificConfiguration);
  }

  private MappingConfiguration<S, D> createRestructure(
      Consumer<MappingConfiguration<S, RD>> restructureMappingConfiguration, boolean applyingSpecificConfiguration) {
    Map<Projection<?, ?>, InternalMapper<?, ?>> mappers = mappingConfiguration.getMappers();
    @SuppressWarnings("unchecked")
    MappingConfiguration<S, RD> config = Mapping.from(mappingConfiguration.getSource())
        .to((Class<RD>) typedPropertyDescriptor.property.getPropertyType());
    // Do not make all source properties mandatory
    config.omitOtherSourceProperties();
    // Inherit registered mappers.
    mappers.entrySet()
        .stream()
        .forEach(entry -> {
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
