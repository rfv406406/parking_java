package com.sideproject.parking_java.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.exception.InvalidParameterError;
import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.service.ParkingLotRegisterService;




@RestController
public class ParkingLotRegisterController {
    @Autowired
    private ParkingLotRegisterService parkingLotRegisterService;

    @GetMapping("/api/parkingLot")
    public ResponseEntity<List<ParkingLot>> getParkingLotRegister() throws DatabaseError {
        List<ParkingLot> parkingLot = parkingLotRegisterService.getParkingLotRegister();
        ResponseEntity<List<ParkingLot>> response = ResponseEntity.status(HttpStatus.OK).body(parkingLot);
        return response;
    }
    

    @PostMapping("/api/parkingLot")
    public ResponseEntity<String> postParkingLotRegister(@ModelAttribute ParkingLot parkingLot) throws InvalidParameterError, DatabaseError {
        parkingLotRegisterService.postParkingLotRegister(parkingLot);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("ok");
        return response;
    }

    @PutMapping("/api/parkingLot")
    public ResponseEntity<String> getParkingLotRegister(@ModelAttribute ParkingLot parkingLot) throws InvalidParameterError, DatabaseError {
        parkingLotRegisterService.putParkingLotRegister(parkingLot);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("ok");
        return response;
    }

    @DeleteMapping("/api/parkingLot/{parkingLotId}")
    public ResponseEntity<String> deleteParkingLotRegister(@PathVariable("parkingLotId") Integer parkingLotId) throws InvalidParameterError, DatabaseError {
        parkingLotRegisterService.deleteParkingLotRegister(parkingLotId);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("ok");
        return response;
    }
    
    
}
