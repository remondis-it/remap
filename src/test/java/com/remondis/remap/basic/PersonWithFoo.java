package com.remondis.remap.basic;

public class PersonWithFoo {
  private String name;
  private PersonWithFoo.Foo foo;

  public PersonWithFoo() {
  }

  public PersonWithFoo(String name, PersonWithFoo.Foo foo) {
    this.name = name;
    this.foo = foo;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public PersonWithFoo.Foo getFoo() {
    return foo;
  }

  public void setFoo(PersonWithFoo.Foo foo) {
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
