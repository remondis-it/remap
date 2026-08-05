package com.remondis.remap.mapInto;

import java.util.List;

/**
 * A destination container holding a collection of record elements for testing mapInto collection key matching with
 * records.
 */
public class PersonWithRecordAddresses {

  private Integer age;
  private String forename;
  private String lastname;

  private List<AddressRecord> addresses;

  public PersonWithRecordAddresses(Integer age, String forename, String lastname, List<AddressRecord> addresses) {
    super();
    this.age = age;
    this.forename = forename;
    this.lastname = lastname;
    this.addresses = addresses;
  }

  public PersonWithRecordAddresses() {
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

  public List<AddressRecord> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<AddressRecord> addresses) {
    this.addresses = addresses;
  }

}
