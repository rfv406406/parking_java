package com.sideproject.parking_java.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.Exception.DatabaseError;
import com.sideproject.parking_java.Exception.InvalidParameterError;
import com.sideproject.parking_java.Model.ParkingLot;
import com.sideproject.parking_java.Service.ParkingLotRegisterService;


@RestController
public class ParkingLotRegisterController {
    @Autowired
    private ParkingLotRegisterService parkingLotRegisterService;

    @PostMapping("/api/parkingLotRegister")
    public ResponseEntity<String> postParkingLotRegister(@ModelAttribute ParkingLot parkingLot) throws InvalidParameterError, DatabaseError {
        parkingLotRegisterService.postParkingLotRegister(parkingLot);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("ok");
        return response;
    }
    
    
}
