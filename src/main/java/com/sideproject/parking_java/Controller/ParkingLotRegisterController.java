package com.sideproject.parking_java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.exception.InvalidParameterError;
import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.service.ParkingLotRegisterService;



@RestController
public class ParkingLotRegisterController {
    @Autowired
    private ParkingLotRegisterService parkingLotRegisterService;

    @PutMapping("/api/parkingLotRegister")
    public ResponseEntity<String> getParkingLotRegister(@ModelAttribute ParkingLot parkingLot) throws InvalidParameterError, DatabaseError {
        parkingLotRegisterService.postParkingLotRegister(parkingLot);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("ok");
        return response;
    }

    @PostMapping("/api/parkingLotRegister")
    public ResponseEntity<String> postParkingLotRegister(@ModelAttribute ParkingLot parkingLot) throws InvalidParameterError, DatabaseError {
        parkingLotRegisterService.postParkingLotRegister(parkingLot);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("ok");
        return response;
    }

    @DeleteMapping("/api/parkingLotRegister")
    public ResponseEntity<String> deleteParkingLotRegister(@RequestBody ParkingLot parkingLot) throws InvalidParameterError, DatabaseError {
        parkingLotRegisterService.dateteParkingLotRegister(parkingLot);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("ok");
        return response;
    }
    
    
}
