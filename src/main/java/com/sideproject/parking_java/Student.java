package com.sideproject.parking_java;

public class Student {
    private Integer number;
    private String name;
    private String email;

    public Student(Integer number, String name, String email) {
        this.number = number;
        this.name = name;
        this.email = email;
    }

    public Integer getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
