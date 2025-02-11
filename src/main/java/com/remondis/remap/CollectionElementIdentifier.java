package com.remondis.remap;

import java.util.Collection;

public class CollectionElementIdentifier<RS, ES> {

  private TypedPropertyDescriptor<Collection<RS>> sourceProperty;
  private TypedPropertyDescriptor<ES> elementProperty;

  public CollectionElementIdentifier(TypedPropertyDescriptor<Collection<RS>> sourceProperty,
      TypedPropertyDescriptor<ES> elementProperty) {
    this.sourceProperty = sourceProperty;
    this.elementProperty = elementProperty;
  }

}
