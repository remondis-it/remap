package com.remondis.remap.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.remondis.remap.basic.PersonWithAddress;
import com.remondis.remap.basic.PersonWithAddress.Address;
import org.junit.jupiter.api.Test;

import com.remondis.remap.Mapper;
import com.remondis.remap.Mapping;
import com.remondis.remap.MappingException;

class MapperTests {

  @Test
  void mapsBeansWithSameFields() {
    Mapper<BeanWithConstructors, BeanWithEmptyConstructor> mapper = Mapping.from(BeanWithConstructors.class)
        .to(BeanWithEmptyConstructor.class)
        .mapper();
    BeanWithEmptyConstructor result = mapper.map(new BeanWithConstructors(42, "42"));
    assertEquals(42, result.getIntField());
    assertEquals("42", result.getStringField());
  }

  @Test
  void mapsWithDefaultConstructor() {
    // when a bean does not have a constructor, it should work with using the
    // default constructor
    Mapper<BeanWithConstructors, BeanWithoutConstructor> mapper = Mapping.from(BeanWithConstructors.class)
        .to(BeanWithoutConstructor.class)
        .mapper();
    BeanWithoutConstructor result = mapper.map(new BeanWithConstructors(42, "42"));
    assertEquals(42, result.getIntField());
    assertEquals("42", result.getStringField());
  }

  @Test
  void failsWithoutEmptyConstructor() {
    // when a bean does not have an empty constructor, mapping should fail
    assertThrows(MappingException.class, () -> Mapping.from(BeanWithConstructors.class)
        .to(BeanWithoutEmptyConstructor.class)
        .mapper());
  }

  @Test
  void failsOnUnspecifiedFields() {
    assertThrows(MappingException.class, () -> Mapping.from(BeanWithConstructors.class)
        .to(Person.class)
        .mapper());
  }

  @Test
  void reassignsFields() {
    Mapper<BeanWithConstructors, Person> mapper = Mapping.from(BeanWithConstructors.class)
        .to(Person.class)
        .reassign(BeanWithConstructors::getIntField)
        .to(Person::getAge)
        .reassign(BeanWithConstructors::getStringField)
        .to(Person::getName)
        .mapper();

    Person person = mapper.map(new BeanWithConstructors(42, "42"));
    assertEquals(42, person.getAge());
    assertEquals("42", person.getName());
  }

  @Test
  void omitsSourceField() {
    Mapper<Person, Name> mapper = Mapping.from(Person.class)
        .to(Name.class)
        .omitInSource(Person::getAge)
        .mapper();

    Name name = mapper.map(new Person("Bob", 42));
    assertEquals("Bob", name.getName());
  }

  @Test
  void omitsDestinationField() {
    Mapper<Name, Person> mapper = Mapping.from(Name.class)
        .to(Person.class)
        .omitInDestination(Person::getAge)
        .mapper();

    Person person = mapper.map(new Name("Bob"));
    assertEquals("Bob", person.getName());
  }

  @Test
  void failsOnMissingNestedMapper() {
    assertThrows(MappingException.class, () -> Mapping.from(PersonWithAddress.class)
        .to(HumanWithAddress.class)
        .mapper());
  }

  @Test
  void mapsNested() {
    Mapper<PersonWithAddress, HumanWithAddress> mapper = Mapping.from(PersonWithAddress.class)
        .to(HumanWithAddress.class)
        .useMapper(Mapping.from(Address.class)
            .to(HumanWithAddress.Address.class)
            .mapper())
        .mapper();

    PersonWithAddress person = new PersonWithAddress();
    person.setName("Bob");
    person.setAddress(new Address("Elm Street"));

    HumanWithAddress human = mapper.map(person);
    assertEquals("Bob", human.getName());
    assertEquals("Elm Street", human.getAddress()
        .getStreet());
  }

  @Test
  void replaces() {
    Mapper<PersonWithAddress, PersonWithFoo> mapper = Mapping.from(PersonWithAddress.class)
        .to(PersonWithFoo.class)
        .replace(PersonWithAddress::getAddress, PersonWithFoo::getFoo)
        .with(address -> new PersonWithFoo.Foo(address.getStreet()))
        .mapper();

    PersonWithAddress person = new PersonWithAddress();
    person.setName("Bob");
    person.setAddress(new Address("Elm Street"));

    PersonWithFoo personWithFoo = mapper.map(person);
    assertEquals("Bob", personWithFoo.getName());
    assertEquals("Elm Street", personWithFoo.getFoo()
        .getBar());
  }

  public static class BeanWithoutConstructor {

    private int intField;
    private String stringField;

    public int getIntField() {
      return intField;
    }

    public void setIntField(int intField) {
      this.intField = intField;
    }

    public String getStringField() {
      return stringField;
    }

    public void setStringField(String stringField) {
      this.stringField = stringField;
    }
  }

  public static class BeanWithoutEmptyConstructor {

    private int intField;
    private String stringField;

    public BeanWithoutEmptyConstructor(int intField, String stringField) {
      this.intField = intField;
      this.stringField = stringField;
    }

    public int getIntField() {
      return intField;
    }

    public void setIntField(int intField) {
      this.intField = intField;
    }

    public String getStringField() {
      return stringField;
    }

    public void setStringField(String stringField) {
      this.stringField = stringField;
    }
  }

  public static class BeanWithConstructors {

    private int intField;
    private String stringField;

    public BeanWithConstructors(int intField, String stringField) {
      this.intField = intField;
      this.stringField = stringField;
    }

    public BeanWithConstructors() {
    }

    public int getIntField() {
      return intField;
    }

    public void setIntField(int intField) {
      this.intField = intField;
    }

    public String getStringField() {
      return stringField;
    }

    public void setStringField(String stringField) {
      this.stringField = stringField;
    }
  }

  public static class BeanWithEmptyConstructor {

    private int intField;
    private String stringField;

    public BeanWithEmptyConstructor() {
    }

    public int getIntField() {
      return intField;
    }

    public void setIntField(int intField) {
      this.intField = intField;
    }

    public String getStringField() {
      return stringField;
    }

    public void setStringField(String stringField) {
      this.stringField = stringField;
    }
  }

  public static class Person {

    private String name;
    private int age;

    public Person(String name, int age) {
      this.name = name;
      this.age = age;
    }

    public Person() {
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
  }

  public static class Name {

    private String name;

    public Name(String name) {
      this.name = name;
    }

    public Name() {
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }

  public static class HumanWithAddress {

    private String name;
    private Address address;

    public HumanWithAddress() {
    }

    public HumanWithAddress(String name, Address address) {
      this.name = name;
      this.address = address;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Address getAddress() {
      return address;
    }

    public void setAddress(Address address) {
      this.address = address;
    }

    public static class Address {

      private String street;

      public Address(String street) {
        this.street = street;
      }

      public Address() {
      }

      public String getStreet() {
        return street;
      }

      public void setStreet(String street) {
        this.street = street;
      }
    }
  }

  public static class PersonWithFoo {
    private String name;
    private Foo foo;

    public PersonWithFoo() {
    }

    public PersonWithFoo(String name, Foo foo) {
      this.name = name;
      this.foo = foo;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public Foo getFoo() {
      return foo;
    }

    public void setFoo(Foo foo) {
      this.foo = foo;
    }

    public static class Foo {
      private String bar;

      public Foo() {
      }

      public Foo(String bar) {
        this.bar = bar;
      }

      public String getBar() {
        return bar;
      }

      public void setBar(String bar) {
        this.bar = bar;
      }
    }
  }

}
