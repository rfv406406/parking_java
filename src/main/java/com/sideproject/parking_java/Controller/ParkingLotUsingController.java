package com.sideproject.parking_java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.model.Transaction;
import com.sideproject.parking_java.service.ParkingLotUsingService;



@RestController
public class ParkingLotUsingController {
    @Autowired
    private ParkingLotUsingService parkingLotUsingService;

    @GetMapping("/api/parkingLotUsing")
    public ResponseEntity<ParkingLot> getUnpaidParkingLotUsageDao() {
        ParkingLot unpaidParkingLotUasgeData = parkingLotUsingService.getUnpaidParkingLotUsageService();
        ResponseEntity<ParkingLot> response = ResponseEntity.status(HttpStatus.OK).body(unpaidParkingLotUasgeData);
        return response;
    }
    

    @PostMapping("/api/parkingLotUsing")
    public ResponseEntity<String> postParkingLotUsingController (@RequestBody Transaction transaction) {
        parkingLotUsingService.postParkingLotUsingService(transaction);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");
        
        return response;
    }
    
}
