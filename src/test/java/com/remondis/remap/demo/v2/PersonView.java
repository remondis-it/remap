package com.remondis.remap.demo.v2;

public class PersonView {
  private String forname;
  private String surname;

  private int age;

  private String email;

  public PersonView() {
    super();
  }

  public String getForname() {
    return forname;
  }

  public void setForname(String forname) {
    this.forname = forname;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
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

  @Override
  public String toString() {
    return "PersonView [forname=" + forname + ", surname=" + surname + ", age=" + age + ", email=" + email + "]";
  }

}
