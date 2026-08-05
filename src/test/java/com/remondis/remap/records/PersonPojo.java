package com.remondis.remap.records;

/**
 * Source POJO for POJO-to-Record mapping tests.
 */
public class PersonPojo {

  private String name;
  private int age;
  private String email;
  private boolean active;

  public PersonPojo() {
  }

  public PersonPojo(String name, int age, String email, boolean active) {
    this.name = name;
    this.age = age;
    this.email = email;
    this.active = active;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

}
