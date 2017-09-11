package com.remondis.remap;

public class AResourceWithOneMoreDestinationField {

  private int onlyInAResource;
  private int zahl;
  private String text;
  
  public AResourceWithOneMoreDestinationField(){
    super();
  }
  
  public AResourceWithOneMoreDestinationField(int onlyInA, int zahl, String text) {
    super();
    this.onlyInAResource = onlyInA;
    this.zahl = zahl;
    this.text = text;
  }
  
  public int getOnlyInAResource() {
    return onlyInAResource;
  }

  public void setOnlyInAResource(int onlyInAResource) {
    this.onlyInAResource = onlyInAResource;
  }
  
  public int getZahl() {
    return zahl;
  }


  public void setZahl(int zahl) {
    this.zahl = zahl;
  }


  public String getText() {
    return text;
  }


  public void setText(String text) {
    this.text = text;
  }
}

