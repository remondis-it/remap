package com.remondis.remap.restructure.demo;

public class Family {

  private Person dad;

  public Family() {
  }

  public Family(Person dad) {
    this.dad = dad;
  }

  public Person getDad() {
    return dad;
  }

  public void setDad(Person dad) {
    this.dad = dad;
  }

  @Override
  public String toString() {
    return "Family{" + "dad=" + dad + '}';
  }
}
