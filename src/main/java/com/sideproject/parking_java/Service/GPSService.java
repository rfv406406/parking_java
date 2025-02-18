package com.sideproject.parking_java.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import org.springframework.web.client.RestTemplate;

@Component
public class GPSService {

    @Value("${googleMapKey}")
    private String googleMapKey;

    private RestTemplate restTemplate = new RestTemplate();

    public String getLatAndLngService(String address) {
        System.out.println("address"+address);
        String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s", address, googleMapKey);
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        System.out.println("GPS"+response.getBody());
        return response.getBody();
    }
}
