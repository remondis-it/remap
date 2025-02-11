package com.remondis.remap;

import static com.remondis.remap.Lang.denyNull;

import java.util.Collection;

public class CollectionElementIdentifierBuilder<S, D, RS> {

  private MappingConfiguration<S, D> mapping;

  private TypedPropertyDescriptor<Collection<RS>> sourceProperty;

  public CollectionElementIdentifierBuilder(MappingConfiguration<S, D> mapping,
      TypedPropertyDescriptor<Collection<RS>> sourceProperty) {
    super();
    this.mapping = mapping;
    this.sourceProperty = sourceProperty;
  }

  public <ES> MappingConfiguration<S, D> identifyBy(TypedSelector<ES, RS> identifyingAttributeSelector) {
    denyNull("identifyingAttributeSelector", identifyingAttributeSelector);
    mapping.addCollectionElementIdentifier(sourceProperty, identifyingAttributeSelector);
    return mapping;
  }
}