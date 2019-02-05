package com.remondis.remap.omitAll;

public class A {
  private Long id;
  private String description;
  private String a;
  private String b;

  public A() {
    super();
  }

  public A(Long id, String description, String a, String b) {
    super();
    this.id = id;
    this.description = description;
    this.a = a;
    this.b = b;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getA() {
    return a;
  }

  public void setA(String a) {
    this.a = a;
  }

  public String getB() {
    return b;
  }

  public void setB(String b) {
    this.b = b;
  }

}
