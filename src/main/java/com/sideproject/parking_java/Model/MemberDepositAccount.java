package com.sideproject.parking_java.model;

public class MemberDepositAccount {
    private Integer depositAccountId;
    private Integer deposit;
    private Integer balance;

    public void setDepositAccountId(int depositAccountId) {
        this.depositAccountId = depositAccountId;
    }
    
    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public Integer getDepositAccountId() {
        return depositAccountId;
    }

    public Integer getDeposit() {
        return deposit; 
    }

    public Integer getBalance() {
        return balance;
    }
}

