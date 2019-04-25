package com.remondis.remap.propertypathmapping.withtransformation.collections;

import java.util.List;

import com.remondis.remap.propertypathmapping.Address;

public class Person {

  List<Address> addresses;

  public Person() {
    super();
  }

  public Person(List<Address> addresses) {
    super();
    this.addresses = addresses;
  }

  public List<Address> getAddresses() {
    return addresses;
  }

  public void setAddresses(List<Address> addresses) {
    this.addresses = addresses;
  }

}
