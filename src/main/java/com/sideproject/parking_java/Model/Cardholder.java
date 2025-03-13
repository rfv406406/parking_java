package com.sideproject.parking_java.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Cardholder {
    @JsonProperty("phone_number")
    private String PHONENUMBER = "0987654321";

    @JsonProperty("name")
    private String NAME = "KCW";
    
    @JsonProperty("email")
    private String EMAIL = "KCW@gmail.com";

    public String getPhoneNumber() {
        return PHONENUMBER;
    }
    public String getName() {
        return NAME;
    }
    public String getEmail() {
        return EMAIL;
    }
}
