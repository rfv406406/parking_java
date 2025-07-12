package com.sideproject.parking_java.model;

import org.springframework.web.multipart.MultipartFile;

public class Car {
    private Integer id;
    private String carNumber;
    private MultipartFile carImage;
    private String carImageUrl;

    public void setId(int id) {
        this.id = id;
    }
    
    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public void setCarImage(MultipartFile carImage) {
        this.carImage = carImage;
    }

    public void setCarImageUrl(String carImageUrl) {
        this.carImageUrl = carImageUrl;
    }

    public Integer getId() {
        return id;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public MultipartFile getCarImage() {
        return carImage;
    }

    public String getCarImageUrl() {
        return carImageUrl;
    }
}
