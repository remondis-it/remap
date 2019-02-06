package com.remondis.remap.omitOthers;

public class AResource {
  private String id;
  private String name;
  private String c;
  private String d;
  private String e;

  public AResource() {
    super();
  }

  public AResource(String id, String name, String c, String d, String e) {
    super();
    this.id = id;
    this.name = name;
    this.c = c;
    this.d = d;
    this.e = e;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getC() {
    return c;
  }

  public void setC(String c) {
    this.c = c;
  }

  public String getD() {
    return d;
  }

  public void setD(String d) {
    this.d = d;
  }

  public String getE() {
    return e;
  }

  public void setE(String e) {
    this.e = e;
  }

}
