package com.sideproject.parking_java.model;

import org.springframework.web.multipart.MultipartFile;

public class CarSpaceImage {
    private Integer id;
    private String name;
    private MultipartFile[] value;
    //return type
    private String[] imgUrl;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(MultipartFile[] value) {
        this.value = value;
    }

    public void setImgurl(String[] imgUrl) {
        this.imgUrl = imgUrl;
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MultipartFile[] getValue() {
        return value;
    }

    public String[] getImgurl() {
        return imgUrl;
    }
}