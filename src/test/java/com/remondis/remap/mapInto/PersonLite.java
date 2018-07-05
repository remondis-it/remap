// Generated by delombok at Thu Jun 14 14:59:06 CEST 2018
package com.remondis.remap.mapInto;

import java.util.List;

public class PersonLite {
  private String forename;
  private String lastname;
  private List<AddressLite> addresses;


  @java.lang.SuppressWarnings("all")
  public static class PersonLiteBuilder {
    @java.lang.SuppressWarnings("all")
    private String forename;
    @java.lang.SuppressWarnings("all")
    private String lastname;
    @java.lang.SuppressWarnings("all")
    private List<AddressLite> addresses;

    @java.lang.SuppressWarnings("all")
    PersonLiteBuilder() {
    }

    @java.lang.SuppressWarnings("all")
    public PersonLiteBuilder forename(final String forename) {
      this.forename = forename;
      return this;
    }

    @java.lang.SuppressWarnings("all")
    public PersonLiteBuilder lastname(final String lastname) {
      this.lastname = lastname;
      return this;
    }

    @java.lang.SuppressWarnings("all")
    public PersonLiteBuilder addresses(final List<AddressLite> addresses) {
      this.addresses = addresses;
      return this;
    }

    @java.lang.SuppressWarnings("all")
    public PersonLite build() {
      return new PersonLite(forename, lastname, addresses);
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
      return "PersonLite.PersonLiteBuilder(forename=" + this.forename + ", lastname=" + this.lastname + ", addresses=" + this.addresses + ")";
    }
  }

  @java.lang.SuppressWarnings("all")
  public static PersonLiteBuilder builder() {
    return new PersonLiteBuilder();
  }

  @java.lang.SuppressWarnings("all")
  public PersonLite() {
  }

  @java.lang.SuppressWarnings("all")
  public PersonLite(final String forename, final String lastname, final List<AddressLite> addresses) {
    this.forename = forename;
    this.lastname = lastname;
    this.addresses = addresses;
  }

  @java.lang.SuppressWarnings("all")
  public String getForename() {
    return this.forename;
  }

  @java.lang.SuppressWarnings("all")
  public String getLastname() {
    return this.lastname;
  }

  @java.lang.SuppressWarnings("all")
  public List<AddressLite> getAddresses() {
    return this.addresses;
  }

  @java.lang.SuppressWarnings("all")
  public void setForename(final String forename) {
    this.forename = forename;
  }

  @java.lang.SuppressWarnings("all")
  public void setLastname(final String lastname) {
    this.lastname = lastname;
  }

  @java.lang.SuppressWarnings("all")
  public void setAddresses(final List<AddressLite> addresses) {
    this.addresses = addresses;
  }

  @java.lang.Override
  @java.lang.SuppressWarnings("all")
  public boolean equals(final java.lang.Object o) {
    if (o == this) return true;
    if (!(o instanceof PersonLite)) return false;
    final PersonLite other = (PersonLite) o;
    if (!other.canEqual((java.lang.Object) this)) return false;
    final java.lang.Object this$forename = this.getForename();
    final java.lang.Object other$forename = other.getForename();
    if (this$forename == null ? other$forename != null : !this$forename.equals(other$forename)) return false;
    final java.lang.Object this$lastname = this.getLastname();
    final java.lang.Object other$lastname = other.getLastname();
    if (this$lastname == null ? other$lastname != null : !this$lastname.equals(other$lastname)) return false;
    final java.lang.Object this$addresses = this.getAddresses();
    final java.lang.Object other$addresses = other.getAddresses();
    if (this$addresses == null ? other$addresses != null : !this$addresses.equals(other$addresses)) return false;
    return true;
  }

  @java.lang.SuppressWarnings("all")
  protected boolean canEqual(final java.lang.Object other) {
    return other instanceof PersonLite;
  }

  @java.lang.Override
  @java.lang.SuppressWarnings("all")
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final java.lang.Object $forename = this.getForename();
    result = result * PRIME + ($forename == null ? 43 : $forename.hashCode());
    final java.lang.Object $lastname = this.getLastname();
    result = result * PRIME + ($lastname == null ? 43 : $lastname.hashCode());
    final java.lang.Object $addresses = this.getAddresses();
    result = result * PRIME + ($addresses == null ? 43 : $addresses.hashCode());
    return result;
  }

  @java.lang.Override
  @java.lang.SuppressWarnings("all")
  public java.lang.String toString() {
    return "PersonLite(forename=" + this.getForename() + ", lastname=" + this.getLastname() + ", addresses=" + this.getAddresses() + ")";
  }
}
