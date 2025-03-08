package com.sideproject.parking_java.Model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class ParkingLot {
    private int parkingLotId;
    private String name;
    private String address;
    private String nearLandmark;
    private String openingTimeAm;
    private String openingTimePm;
    private String spaceInOut;
    private int price;
    private String carWidth;
    private String carHeight;
    private String latitude;
    private String longitude;

    private MultipartFile[] img;

    private List<CarSpaceNumber> carSpaceNumber;
    private List<CarSpaceImage> carSpaceImage;

    public void setParkingLotId(int parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNearLandmark(String nearLandmark) {
        this.nearLandmark = nearLandmark;
    }

    public void setOpeningTimeAm(String openingTimeAm) {
        this.openingTimeAm = openingTimeAm;
    }

    public void setOpeningTimePm(String openingTimePm) {
        this.openingTimePm = openingTimePm;
    }

    public void setSpaceInOut(String spaceInOut) {
        this.spaceInOut = spaceInOut;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCarWidth(String carWidth) {
        this.carWidth = carWidth;
    }

    public void setCarHeight(String carHeight) {
        this.carHeight = carHeight;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setImg(MultipartFile[] img) {
        this.img = img;
    }

    public void setCarSpaceNumber(List<CarSpaceNumber> carSpaceNumber) {
        this.carSpaceNumber = carSpaceNumber;
    }

    public void setCarSpaceImage(List<CarSpaceImage> carSpaceImage) {
        this.carSpaceImage = carSpaceImage;
    }

    public int getParkingLotId() {
        return parkingLotId;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getNearLandmark() {
        return nearLandmark;
    }

    public String getOpeningTimeAm() {
        return openingTimeAm;
    }

    public String getOpeningTimePm() {
        return openingTimePm;
    }

    public String getSpaceInOut() {
        return spaceInOut;
    }

    public int getPrice() {
        return price;
    }

    public String getCarWidth() {
        return carWidth;
    }

    public String getCarHeight() {
        return carHeight;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude; 
    }

    public MultipartFile[] getImg() {
        return img;
    }

    public List<CarSpaceNumber> getCarSpaceNumber() {
        return carSpaceNumber;
    }

    public List<CarSpaceImage> getCarSpaceImage() {
        return carSpaceImage;
    }
}