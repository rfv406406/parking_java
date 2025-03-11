package com.sideproject.parking_java.model;

public class CarSpaceNumber {
    private int parkingLotId;
    private String name;
    private String value;
    private String status;

    public void setParkingLotId(int parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getParkingLotId() {
        return parkingLotId;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getStatus() {
        return status;
    }
}
