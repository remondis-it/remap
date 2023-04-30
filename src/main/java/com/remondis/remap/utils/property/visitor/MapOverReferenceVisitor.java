package com.remondis.remap.utils.property.visitor;

import java.util.function.Function;

import com.remondis.remap.utils.property.walker.PropertyAccess;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MapOverReferenceVisitor<T, TT, ID> implements VisitorFunction<T, TT> {
  private final EntityManager entityManager;
  private final Function<TT, ID> idExtractor;

  @Override
  @SuppressWarnings("unchecked")
  public void consume(PropertyAccess<T, TT> access) {
    TT sourceValue = access.sourceProperty()
        .get();
    TT targetValue = access.targetProperty()
        .get();

    TT reference = (TT) entityManager.getReference(targetValue.getClass(), idExtractor.apply(sourceValue));
    access.targetProperty().set(reference);
  }
}
