package com.sideproject.parking_java.model;

public class TappayResponse {
    private Integer status;
    private String msg = "failure";
    private Integer amount;
    
    public Integer getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    
    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    
    public Integer getAmount() {
        return amount;
    }
    public void setAmount(int amount) {
        this.amount = amount;
    }
}
