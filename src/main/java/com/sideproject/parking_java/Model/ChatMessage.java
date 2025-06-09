package com.sideproject.parking_java.model;

import org.springframework.web.multipart.MultipartFile;

public class ChatMessage {
    private Integer id;
    private Integer chatroomId;
    private Integer senderId;
    private Integer recipientId;
    private String message;
    private String timestamp;
    private MultipartFile[] img;
    // return type
    // private ArrayList<String> imgUrl;

    public void setId(Integer id) {
        this.id = id;
    }

    public void setChatroomId(Integer chatroomId) {
        this.chatroomId = chatroomId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setImg(MultipartFile[] img) {
        this.img = img;
    }

    // public void setImgUrl(ArrayList<String> imgUrl) {
    //     this.imgUrl = imgUrl;
    // }

    public Integer getId() {
        return id;
    }

    public Integer getChatroomId() {
        return chatroomId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public MultipartFile[] getImg() {
        return img;
    }

    // public ArrayList<String> getImgUrl() {
    //     return imgUrl;
    // }
}
