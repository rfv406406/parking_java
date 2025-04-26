package com.sideproject.parking_java.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class ParkingLot {
    private Integer parkingLotId;
    private String name;
    private String address;
    private String nearLandmark;
    private String openingTime;
    private String closingTime;
    private String spaceInOut;
    private Integer price;
    private String carWidth;
    private String carHeight;
    private double latitude;
    private double longitude;

    private MultipartFile[] img;
    // return type
    private String[] imgUrl;

    private List<CarSpaceNumber> carSpaceNumber;
    private List<CarSpaceImage> carSpaceImage;

    public void setParkingLotId(Integer parkingLotId) {
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

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
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

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setImg(MultipartFile[] img) {
        this.img = img;
    }

    public void setImgUrl(String[] imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setCarSpaceNumber(List<CarSpaceNumber> carSpaceNumber) {
        this.carSpaceNumber = carSpaceNumber;
    }

    public void setCarSpaceImage(List<CarSpaceImage> carSpaceImage) {
        this.carSpaceImage = carSpaceImage;
    }

    public Integer getParkingLotId() {
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

    public String getOpeningTime() {
        return openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public String getSpaceInOut() {
        return spaceInOut;
    }

    public Integer getPrice() {
        return price;
    }

    public String getCarWidth() {
        return carWidth;
    }

    public String getCarHeight() {
        return carHeight;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude; 
    }

    public MultipartFile[] getImg() {
        return img;
    }

    public String[] getImgUrl() {
        return imgUrl;
    }

    public List<CarSpaceNumber> getCarSpaceNumber() {
        return carSpaceNumber;
    }

    public List<CarSpaceImage> getCarSpaceImage() {
        return carSpaceImage;
    }
}