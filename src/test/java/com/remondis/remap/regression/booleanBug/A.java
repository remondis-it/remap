package com.remondis.remap.regression.booleanBug;

public class A {
  private Boolean newsletterSubscribed;
  private String mail;

  public A() {
    super();
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public Boolean getNewsletterSubscribed() {
    return newsletterSubscribed;
  }

  public void setNewsletterSubscribed(Boolean newsletterSubscribed) {
    this.newsletterSubscribed = newsletterSubscribed;
  }
}
