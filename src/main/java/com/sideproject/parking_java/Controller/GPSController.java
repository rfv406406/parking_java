package com.sideproject.parking_java.controller;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sideproject.parking_java.exception.InvalidParameterError;
import com.sideproject.parking_java.service.GPSService;

@RestController
public class GPSController {
    @Autowired
    private GPSService gpsService;

    @GetMapping("/api/getLatAndLong/{address}")
    public ResponseEntity<String> getLatAndLng(@PathVariable String address) throws InvalidParameterError, JsonProcessingException, JsonMappingException{
        HashMap<String, Object> latLng = gpsService.getLatAndLngService(address);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("GPS" + latLng);
        
        return response;
    }
}
