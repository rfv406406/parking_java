package com.sideproject.parking_java.model;


public class ChatRoom {
    private Integer id;
    private Integer senderId;
    private Integer recipientId;
    private Integer parkingLotId;
    private String parkingLotName;
    private String senderAccount;
    private String recipientAccount;
    private String lastActivity;
    private String lastRead;
    private String lastReceivedMessage;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public void setSenderAccount(String senderAccount) {
        this.senderAccount = senderAccount;
    }

    public void setParkingLotId(Integer parkingLotId) {
        this.parkingLotId = parkingLotId;
    }

    public void setParkingLotName(String parkingLotName) {
        this.parkingLotName = parkingLotName;
    }

    public void setRecipientAccount(String recipientAccount) {
        this.recipientAccount = recipientAccount;
    }

    public void setLastActivity(String lastActivity) {
        this.lastActivity = lastActivity;
    }

    public void setLastRead(String lastRead) {
        this.lastRead = lastRead;
    }

    public void setLastReceivedMessage(String lastReceivedMessage) {
        this.lastReceivedMessage = lastReceivedMessage;
    }


    public Integer getId() {
        return id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public Integer getParkingLotId() {
        return parkingLotId;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public String getParkingLotName() {
        return parkingLotName;
    }

    public String getSenderAccount() {
        return senderAccount;
    }

    public String getRecipientAccount() {
        return recipientAccount;
    }

    public String getLastActivity() {
        return lastActivity;
    }

    public String getLastRead() {
        return lastRead;
    }

    public String getLastReceivedMessage() {
        return lastReceivedMessage;
    }
}
