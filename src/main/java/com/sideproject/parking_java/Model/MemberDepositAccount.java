package com.sideproject.parking_java.model;

public class MemberDepositAccount {
    private int depositAccountId;
    private int deposit;
    private int balance;

    public void setDepositAccountId(int depositAccountId) {
        this.depositAccountId = depositAccountId;
    }
    
    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getDepositAccountId() {
        return depositAccountId;
    }

    public int getDeposit() {
        return deposit; 
    }

    public int getBalance() {
        return balance;
    }
}

