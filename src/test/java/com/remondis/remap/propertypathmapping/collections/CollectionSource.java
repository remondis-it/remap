package com.remondis.remap.propertypathmapping.collections;

import java.util.List;

import com.remondis.remap.propertypathmapping.Person;

public class CollectionSource {

  private List<Person> persons;

  public CollectionSource(List<Person> persons) {
    super();
    this.persons = persons;
  }

  public CollectionSource() {
    super();
  }

  public List<Person> getPersons() {
    return persons;
  }

  public void setPersons(List<Person> persons) {
    this.persons = persons;
  }

}
