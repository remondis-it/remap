package com.remondis.remap.records;

/**
 * POJO with different field names for Record-to-POJO reassign tests.
 */
public class PersonResourcePojo {

  private String fullName;
  private int yearsOld;
  private String emailAddress;
  private boolean isActive;

  public PersonResourcePojo() {
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public int getYearsOld() {
    return yearsOld;
  }

  public void setYearsOld(int yearsOld) {
    this.yearsOld = yearsOld;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public boolean isIsActive() {
    return isActive;
  }

  public void setIsActive(boolean isActive) {
    this.isActive = isActive;
  }

}
