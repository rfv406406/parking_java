package com.sideproject.parking_java.model;

import org.springframework.web.multipart.MultipartFile;

public class CarSpaceImage {
    private String name;
    private MultipartFile[] value;

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(MultipartFile[] value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public MultipartFile[] getValue() {
        return value;
    }
}