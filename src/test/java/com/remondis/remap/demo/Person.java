package com.remondis.remap.demo;

public class Person {

  private String salutation;
  private String forename;
  private String name;
  private Gender gender;
  private double bodyHeight;

  public Person(String salutation, String forename, String name, Gender gender, double bodyHeight) {
    super();
    this.salutation = salutation;
    this.forename = forename;
    this.name = name;
    this.gender = gender;
    this.bodyHeight = bodyHeight;
  }

  public Person() {
    super();
  }

  public String getSalutation() {
    return salutation;
  }

  public void setSalutation(String salutation) {
    this.salutation = salutation;
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

  public Gender getGender() {
    return gender;
  }

  public void setGender(Gender gender) {
    this.gender = gender;
  }

  public double getBodyHeight() {
    return bodyHeight;
  }

  public void setBodyHeight(double bodyHeight) {
    this.bodyHeight = bodyHeight;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    long temp;
    temp = Double.doubleToLongBits(bodyHeight);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + ((forename == null) ? 0 : forename.hashCode());
    result = prime * result + ((gender == null) ? 0 : gender.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((salutation == null) ? 0 : salutation.hashCode());
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
    if (Double.doubleToLongBits(bodyHeight) != Double.doubleToLongBits(other.bodyHeight))
      return false;
    if (forename == null) {
      if (other.forename != null)
        return false;
    } else if (!forename.equals(other.forename))
      return false;
    if (gender != other.gender)
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    if (salutation == null) {
      if (other.salutation != null)
        return false;
    } else if (!salutation.equals(other.salutation))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "Person [salutation=" + salutation + ", forename=" + forename + ", name=" + name + ", gender=" + gender
        + ", bodyHeight=" + bodyHeight + "]";
  }

}
