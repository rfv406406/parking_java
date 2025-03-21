package com.sideproject.parking_java.model;

public class Transaction {
    private int id;
    private String orderNumber;
    private int memberId;
    private int carId;
    private int depositAccountId;
    private int parkingLotId;
    private int parkingLotSquareId;
    private String startTime;
    private String stopTime;
    private String transactionType;
    private int deposit;
    private int consumption;
    private int income;
    private String status;
    private String transactionsTime;

    public void setId(int id) {
        this.id = id;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public void setDepositAccountId(int depositAccountId) {
        this.depositAccountId = depositAccountId;
    }

    public void setParkingLotId(int parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    public void setParkingLotSquareId(int parkingLotSquareId) {
        this.parkingLotSquareId = parkingLotSquareId;
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

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public void setConsumption(int consumption) {
        this.consumption = consumption;
    }

    public void setIncome(int income) {
        this.income = income;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTransactionsTime(String transactionsTime) {
        this.transactionsTime = transactionsTime;
    }

    public int getId() {
        return this.id;
    }
    
    public String getOrderNumber() {
        return this.orderNumber;
    }
    
    public int getMemberId() {
        return this.memberId;
    }
    
    public int getCarId() {
        return this.carId;
    }
    
    public int getDepositAccountId() {
        return this.depositAccountId;
    }
    
    public int getParkingLotId() {
        return this.parkingLotId;
    }
    
    public int getParkingLotSquareId() {
        return this.parkingLotSquareId;
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
    
    public int getDeposit() {
        return this.deposit;
    }
    
    public int getConsumption() {
        return this.consumption;
    }
    
    public int getIncome() {
        return this.income;
    }
    
    public String getStatus() {
        return this.status;
    }
    
    public String getTransactionsTime() {
        return this.transactionsTime;
    }
}
