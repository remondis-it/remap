// Generated by delombok at Thu Jun 14 14:59:06 CEST 2018
package com.remondis.remap.flatCollectionMapping;

public class Id {
  private Long id;


  @java.lang.SuppressWarnings("all")
  public static class IdBuilder {
    @java.lang.SuppressWarnings("all")
    private Long id;

    @java.lang.SuppressWarnings("all")
    IdBuilder() {
    }

    @java.lang.SuppressWarnings("all")
    public IdBuilder id(final Long id) {
      this.id = id;
      return this;
    }

    @java.lang.SuppressWarnings("all")
    public Id build() {
      return new Id(id);
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
      return "Id.IdBuilder(id=" + this.id + ")";
    }
  }

  @java.lang.SuppressWarnings("all")
  public static IdBuilder builder() {
    return new IdBuilder();
  }

  @java.lang.SuppressWarnings("all")
  public Long getId() {
    return this.id;
  }

  @java.lang.SuppressWarnings("all")
  public void setId(final Long id) {
    this.id = id;
  }

  @java.lang.Override
  @java.lang.SuppressWarnings("all")
  public boolean equals(final java.lang.Object o) {
    if (o == this) return true;
    if (!(o instanceof Id)) return false;
    final Id other = (Id) o;
    if (!other.canEqual((java.lang.Object) this)) return false;
    final java.lang.Object this$id = this.getId();
    final java.lang.Object other$id = other.getId();
    if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
    return true;
  }

  @java.lang.SuppressWarnings("all")
  protected boolean canEqual(final java.lang.Object other) {
    return other instanceof Id;
  }

  @java.lang.Override
  @java.lang.SuppressWarnings("all")
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final java.lang.Object $id = this.getId();
    result = result * PRIME + ($id == null ? 43 : $id.hashCode());
    return result;
  }

  @java.lang.Override
  @java.lang.SuppressWarnings("all")
  public java.lang.String toString() {
    return "Id(id=" + this.getId() + ")";
  }

  @java.lang.SuppressWarnings("all")
  public Id() {
  }

  @java.lang.SuppressWarnings("all")
  public Id(final Long id) {
    this.id = id;
  }
}
