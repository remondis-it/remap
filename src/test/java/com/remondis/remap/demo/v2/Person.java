package com.remondis.remap.demo.v2;

import java.time.LocalDate;

public class Person {
  private String forname;
  private String name;

  private LocalDate birthday; // Optional.

  private Address address; // Optional.

  private String taxId;

  public Person() {
    super();
  }

  public Person(String name, String forname, LocalDate birthday, Address address, String taxId) {
    super();
    this.name = name;
    this.forname = forname;
    this.birthday = birthday;
    this.address = address;
    this.taxId = taxId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getForname() {
    return forname;
  }

  public void setForname(String forname) {
    this.forname = forname;
  }

  public LocalDate getBirthday() {
    return birthday;
  }

  public void setBirthday(LocalDate birthday) {
    this.birthday = birthday;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  public String getTaxId() {
    return taxId;
  }

  public void setTaxId(String taxId) {
    this.taxId = taxId;
  }

  @Override
  public String toString() {
    return "Person [name=" + name + ", forname=" + forname + ", birthday=" + birthday + ", taxId=" + taxId + "]";
  }

}
