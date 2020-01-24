package com.remondis.remap;

import java.util.Collection;

/**
 * Facade for {@link AssertConfiguration} to specify assertions for a restructuring mapper configuration.
 * 
 * @param <S> The source type.
 * @param <D> The destination type.
 */
public class RestructuringAssertConfiguration<S, D> {

  private final AssertConfiguration<S, D> delegate;

  RestructuringAssertConfiguration(AssertConfiguration<S, D> delegate) {
    this.delegate = delegate;
  }

  AssertConfiguration<S, D> getDelegate() {
    return delegate;
  }

  public void ensure() throws AssertionError {
    throw new UnsupportedOperationException(
        "Do not call ensure() here - this will be done by the parent mapping assertion.");
  }

  public <RS> ReassignAssertBuilder<S, D, RS> expectReassign(FieldSelector<S> sourceSelector) {
    return delegate.expectReassign(sourceSelector);
  }

  public <RD, RS> ReplaceAssertBuilder<S, D, RD, RS> expectReplace(TypedSelector<RS, S> sourceSelector,
      TypedSelector<RD, D> destinationSelector) {
    return delegate.expectReplace(sourceSelector, destinationSelector);
  }

  public <RD> SetAssertBuilder<S, D, RD> expectSet(TypedSelector<RD, D> destinationSelector) {
    return delegate.expectSet(destinationSelector);
  }

  public <RD> RestructureAssertBuilder<S, D, RD> expectRestructure(TypedSelector<RD, D> destinationSelector) {
    return delegate.expectRestructure(destinationSelector);
  }

  public <RD, RS> ReplaceCollectionAssertBuilder<S, D, RD, RS> expectReplaceCollection(
      TypedSelector<Collection<RS>, S> sourceSelector, TypedSelector<Collection<RD>, D> destinationSelector) {
    return delegate.expectReplaceCollection(sourceSelector, destinationSelector);
  }

  public AssertConfiguration<S, D> expectOmitInSource(FieldSelector<S> sourceSelector) {
    return delegate.expectOmitInSource(sourceSelector);
  }

  public AssertConfiguration<S, D> expectNoImplicitMappings() {
    return delegate.expectNoImplicitMappings();
  }

  public AssertConfiguration<S, D> expectOmitInDestination(FieldSelector<D> destinationSelector) {
    return delegate.expectOmitInDestination(destinationSelector);
  }

  public AssertConfiguration<S, D> expectOthersToBeOmitted() {
    return delegate.expectOthersToBeOmitted();
  }

  public AssertConfiguration<S, D> expectOtherSourceFieldsToBeOmitted() {
    return delegate.expectOtherSourceFieldsToBeOmitted();
  }

  public AssertConfiguration<S, D> expectOtherDestinationFieldsToBeOmitted() {
    return delegate.expectOtherDestinationFieldsToBeOmitted();
  }

}
