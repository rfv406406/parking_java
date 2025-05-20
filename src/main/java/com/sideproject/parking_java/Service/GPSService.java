package com.sideproject.parking_java.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sideproject.parking_java.exception.InvalidParameterError;

import java.util.HashMap;

import org.springframework.stereotype.Service;


@Service
public class GpsService {

    @Value("${googleMapKey}")
    private String googleMapKey;

    public HashMap<String, Object> getLatAndLngService(String address) throws InvalidParameterError, Exception, JsonProcessingException{
        if (address == null || address.isEmpty()) {
            throw new InvalidParameterError("address is null or empty");
        }
        try {
            String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=%s", address, googleMapKey);
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> GPS = restTemplate.getForEntity(url, String.class);
            // 建立 ObjectMapper
            ObjectMapper mapper = new ObjectMapper();
            // 解析 JSON 字串為 JsonNode tree
            JsonNode root = mapper.readTree(GPS.getBody());

            // 取得 results 陣列的第一個物件
            JsonNode firstResult = root.path("results").get(0);
            // 取得 geometry -> location
            JsonNode location = firstResult.path("geometry").path("location");

            double lat = location.path("lat").asDouble();
            double lng = location.path("lng").asDouble();

            HashMap<String, Object> map = new HashMap<>();
            map.put("lat",lat);
            map.put("lng",lng);

            return map;
        } catch (Exception e) {
            throw new Exception("gps not found");
        }
    }
}
