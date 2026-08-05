package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.util.ArrayDeque;
import java.util.Deque;
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

  /**
   * Tracks the (source, destination-field-type) projections whose restructure configuration is currently being
   * built on the current thread. A {@link Consumer} passed to {@link #applying(Consumer)} that recursively
   * restructures back into the very field type it is currently configuring - the natural way to express a
   * self-referential tree or linked structure - would otherwise recurse synchronously during configuration until a
   * {@link StackOverflowError} crashes the current thread, instead of failing with a clear diagnostic.
   */
  private static final ThreadLocal<Deque<Projection<?, ?>>> RESTRUCTURE_BUILD_STACK = ThreadLocal
      .withInitial(ArrayDeque::new);

  private MappingConfiguration<S, D> createRestructure(
      Consumer<MappingConfiguration<S, RD>> restructureMappingConfiguration, boolean applyingSpecificConfiguration) {
    @SuppressWarnings("unchecked")
    Class<RD> fieldType = (Class<RD>) typedPropertyDescriptor.property.getPropertyType();
    Projection<S, RD> projection = new Projection<>(mappingConfiguration.getSource(), fieldType);
    Deque<Projection<?, ?>> buildStack = RESTRUCTURE_BUILD_STACK.get();
    if (buildStack.contains(projection)) {
      throw MappingException.cyclicMapperConfiguration(projection, buildStack);
    }
    buildStack.push(projection);
    try {
      MappingConfiguration<S, RD> config = Mapping.from(mappingConfiguration.getSource())
          .to(fieldType);
      // Do not make all source properties mandatory
      config.omitOtherSourceProperties();
      // Inherit registered mappers from the enclosing configuration. This is a live link, not a snapshot: mappers
      // registered on the enclosing configuration via useMapper() after this restructure() call are resolved lazily
      // when the nested mapper is validated, so the order of useMapper()/restructure() calls does not matter.
      config.setParentMapperRegistry(mappingConfiguration);
      restructureMappingConfiguration.accept(config);
      Transformation restructureTransformation = new RestructureTransformation<>(config, null,
          typedPropertyDescriptor.property, null, applyingSpecificConfiguration);
      mappingConfiguration.addDestinationMapping(typedPropertyDescriptor.property, restructureTransformation);
      return mappingConfiguration;
    } finally {
      buildStack.pop();
      if (buildStack.isEmpty()) {
        RESTRUCTURE_BUILD_STACK.remove();
      }
    }
  }

}
