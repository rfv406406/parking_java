package com.sideproject.parking_java.model;

public class CarSpaceNumber {
    private Integer id;
    private Integer parkingLotId;
    private String name;
    private String value;
    private String status;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setParkingLotId(Integer parkingLotId) {
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

    public Integer getId() {
        return id;
    }

    public Integer getParkingLotId() {
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
