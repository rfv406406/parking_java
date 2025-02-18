package com.sideproject.parking_java.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.Service.GPSService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
public class GPSController {
    @Autowired
    private GPSService gpsService;

    @GetMapping("/api/getLatAndLong/{address}")
    public ResponseEntity<String> getLatAndLng(@PathVariable String address) {
        String GPS = gpsService.getLatAndLngService(address);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("address" + GPS);
        return response;
    }
}
