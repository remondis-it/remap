package com.remondis.remap.mapInto;

import java.util.List;

public class PersonLite {

  private String forename;
  private String lastname;

  private List<AddressLite> addresses;

  public PersonLite(String forename, String lastname, List<AddressLite> addresses) {
    super();
    this.forename = forename;
    this.lastname = lastname;
    this.addresses = addresses;
  }

  public PersonLite() {
    super();
  }

  public String getForename() {
    return forename;
  }

  public void setForename(String forename) {
    this.forename = forename;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public List<AddressLite> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<AddressLite> addresses) {
    this.addresses = addresses;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((addresses == null) ? 0 : addresses.hashCode());
    result = prime * result + ((forename == null) ? 0 : forename.hashCode());
    result = prime * result + ((lastname == null) ? 0 : lastname.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PersonLite other = (PersonLite) obj;
    if (addresses == null) {
      if (other.addresses != null)
        return false;
    } else if (!addresses.equals(other.addresses))
      return false;
    if (forename == null) {
      if (other.forename != null)
        return false;
    } else if (!forename.equals(other.forename))
      return false;
    if (lastname == null) {
      if (other.lastname != null)
        return false;
    } else if (!lastname.equals(other.lastname))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "PersonLite [forename=" + forename + ", lastname=" + lastname + ", addresses=" + addresses + "]";
  }

}
