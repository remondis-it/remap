package com.remondis.remap.restructure;

public class Source {

    private String forename;
    private String name;

    private String street;
    private int houseNumber;
    private String city;

    public Source() {
    }

    public Source(String forename, String name, String street, int houseNumber, String city) {
        this.forename = forename;
        this.name = name;
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
    }


    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Override
    public String toString() {
        return "Source{" +
                "forename='" + forename + '\'' +
                ", name='" + name + '\'' +
                ", street='" + street + '\'' +
                ", houseNumber=" + houseNumber +
                ", city='" + city + '\'' +
                '}';
    }
}


