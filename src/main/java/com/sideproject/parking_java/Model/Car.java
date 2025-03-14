package com.sideproject.parking_java.model;

import org.springframework.web.multipart.MultipartFile;

public class Car {
    private String carNumber;
    private MultipartFile carImage;
    
    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public void setCarImage(MultipartFile carImage) {
        this.carImage = carImage;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public MultipartFile getCarImage() {
        return carImage;
    }
}
