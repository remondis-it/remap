package com.remondis.remap.mapInto;

import java.util.List;

public class Person {

  private Integer age;
  private String forename;
  private String lastname;

  private List<Address> addresses;

  public Person(Integer age, String forename, String lastname, List<Address> addresses) {
    super();
    this.age = age;
    this.forename = forename;
    this.lastname = lastname;
    this.addresses = addresses;
  }

  public Person() {
    super();
  }

  public Integer getAge() {
    return age;
  }

  public void setAge(Integer age) {
    this.age = age;
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

  public List<Address> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<Address> addresses) {
    this.addresses = addresses;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((addresses == null) ? 0 : addresses.hashCode());
    result = prime * result + ((age == null) ? 0 : age.hashCode());
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
    Person other = (Person) obj;
    if (addresses == null) {
      if (other.addresses != null)
        return false;
    } else if (!addresses.equals(other.addresses))
      return false;
    if (age == null) {
      if (other.age != null)
        return false;
    } else if (!age.equals(other.age))
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
    return "Person [age=" + age + ", forename=" + forename + ", lastname=" + lastname + ", addresses=" + addresses
        + "]";
  }

}
