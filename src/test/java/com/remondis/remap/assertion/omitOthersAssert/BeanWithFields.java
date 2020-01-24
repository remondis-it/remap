package com.remondis.remap.assertion.omitOthersAssert;

public class BeanWithFields {
    private String string;
    private int number;

    public BeanWithFields() {
    }

    public BeanWithFields(String string, int number) {
        this.string = string;
        this.number = number;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
