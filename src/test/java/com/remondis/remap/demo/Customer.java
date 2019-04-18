package com.remondis.remap.demo;

public class Customer {

  private String title;
  private String forename;
  private String name;
  private String gender;
  private String address;

  public Customer() {
    super();
  }

  public Customer(String title, String forename, String name, String gender, String address) {
    super();
    this.title = title;
    this.forename = forename;
    this.name = name;
    this.gender = gender;
    this.address = address;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getForename() {
    return forename;
  }

  public void setForename(String forename) {
    this.forename = forename;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

}
