package com.sideproject.parking_java.model;

public class Transaction {
    private Integer id;
    private String orderNumber;
    private Integer memberId;
    private Integer carId;
    private Integer depositAccountId;
    private Integer parkingLotId;
    private Integer parkingLotSquareId;
    private Integer price;
    private String startTime;
    private String stopTime;
    private String transactionType;
    private Integer amount;
    private String status;
    private String transactionsTime;
    private ParkingLot parkingLot;
    private Car car;

    public void setId(int id) {
        this.id = id;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public void setDepositAccountId(int depositAccountId) {
        this.depositAccountId = depositAccountId;
    }

    public void setParkingLotId(Integer parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    public void setParkingLotSquareId(int parkingLotSquareId) {
        this.parkingLotSquareId = parkingLotSquareId;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTransactionsTime(String transactionsTime) {
        this.transactionsTime = transactionsTime;
    }

    public void setParkingLot(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Integer getId() {
        return this.id;
    }
    
    public String getOrderNumber() {
        return this.orderNumber;
    }
    
    public Integer getMemberId() {
        return this.memberId;
    }
    
    public Integer getCarId() {
        return this.carId;
    }
    
    public Integer getDepositAccountId() {
        return this.depositAccountId;
    }
    
    public Integer getParkingLotId() {
        return this.parkingLotId;
    }
    
    public Integer getParkingLotSquareId() {
        return this.parkingLotSquareId;
    }

    public Integer getPrice() {
        return this.price;
    }
    
    public String getStartTime() {
        return this.startTime;
    }
    
    public String getStopTime() {
        return this.stopTime;
    }
    
    public String getTransactionType() {
        return this.transactionType;
    }
    
    public Integer getAmount() {
        return this.amount;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public String getTransactionsTime() {
        return this.transactionsTime;
    }

    public ParkingLot getParkingLot() {
        return this.parkingLot;
    }

    public Car getCar() {
        return this.car;
    }
}
