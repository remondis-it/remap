
package com.remondis.remap.bidirectional;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.remondis.remap.BidirectionalMapper;
import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;

public class BidirectionalDemo {

  @Test
  public void bidirectionalDemo() {
    Mapper<Person, Human> to = Mapping.from(Person.class)
        .to(Human.class)
        .mapper();
    Mapper<Human, Person> from = Mapping.from(Human.class)
        .to(Person.class)
        .mapper();
    BidirectionalMapper<Person, Human> bidirectionalMapper = BidirectionalMapper.of(to, from);

    Person person = new Person("Christopher");
    Human human = bidirectionalMapper.map(person);
    Person mappedBackToPerson = bidirectionalMapper.mapFrom(human);
    assertEquals(person, mappedBackToPerson);

  }

  public static class Person {
    private String name;

    public Person() {
    }

    public Person(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
      if (name == null) {
        if (other.name != null)
          return false;
      } else if (!name.equals(other.name))
        return false;
      return true;
    }

  }

  public static class Human {
    private String name;

    public Human() {
    }

    public Human(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
      Human other = (Human) obj;
      if (name == null) {
        if (other.name != null)
          return false;
      } else if (!name.equals(other.name))
        return false;
      return true;
    }

  }

}
