package com.sideproject.parking_java.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.exception.InvalidParameterError;
import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.service.ParkingLotRegisterService;




@RestController
public class ParkingLotRegisterController {

    @Autowired
    private ParkingLotRegisterService parkingLotRegisterService;

    @GetMapping("/api/parkingLot")
    public ResponseEntity<Map<String, ParkingLot>> getParkingLotRegister(
            @RequestParam(required = false) Integer memberId,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Integer distance,
            @RequestParam(required = false) Integer price,
            @RequestParam(required = false) Integer carWidth,
            @RequestParam(required = false) Integer carHeight
        ) throws DatabaseError {
        Map<String, ParkingLot> parkingLotMap = parkingLotRegisterService.getParkingLotRegister(memberId, lng, lat, distance, price, carWidth, carHeight);
        ResponseEntity<Map<String, ParkingLot>> response = ResponseEntity.status(HttpStatus.OK).body(parkingLotMap);
        return response;
    }
    

    @PostMapping("/api/parkingLot")
    public ResponseEntity<String> postParkingLotRegister(@ModelAttribute ParkingLot parkingLot) throws InvalidParameterError, DatabaseError, JsonProcessingException, Exception {
        parkingLotRegisterService.postParkingLotRegister(parkingLot);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");
        return response;
    }

    @PutMapping("/api/parkingLot/{parkingLotId}")
    public ResponseEntity<String> getParkingLotRegister(@PathVariable Integer parkingLotId, @ModelAttribute ParkingLot parkingLot) throws InvalidParameterError, DatabaseError, JsonProcessingException, Exception {
        parkingLotRegisterService.putParkingLotRegister(parkingLotId, parkingLot);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");
        return response;
    }

    @DeleteMapping("/api/parkingLot/{parkingLotId}")
    public ResponseEntity<String> deleteParkingLotRegister(@PathVariable Integer parkingLotId) throws InvalidParameterError, DatabaseError {
        parkingLotRegisterService.deleteParkingLotRegister(parkingLotId);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");
        return response;
    }
    
    
}
