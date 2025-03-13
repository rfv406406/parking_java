package com.sideproject.parking_java.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TappayPayload {
    private String prime;

    @JsonProperty("partner_key")
    private String PARTNER_KEY = "partner_HhmDLgk9cfgfzexAH1atrizRHAfXrRsLshDvpu6S6tW9m4rc0dtrE3cP";

    @JsonProperty("merchant_id")
    private String MERCHANT_ID = "rfv406406_CTBC";

    @JsonProperty("details")
    private String DETAILS = "TapPay Test";

    private String amount;
    private String orderNumber;
    private Cardholder cardholder = new Cardholder();

    @JsonProperty("remember")
    private boolean REMEMBER = true;

    public void setPrime(String prime) {
        this.prime = prime;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getPrime() {
        return prime;
    }

    public String getAmount() {
        return amount;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public String getPartnerKey() {
        return PARTNER_KEY;
    }

    public String getMerchantId() {
        return MERCHANT_ID;
    }

    public String getDetails() {
        return DETAILS;
    }

    public Cardholder getCardholder() {
        return cardholder;
    }

    public boolean  getRemember() {
        return REMEMBER;
    }
}
