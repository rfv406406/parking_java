package com.sideproject.parking_java.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.model.Transaction;
import com.sideproject.parking_java.service.TransactionService;

@RestController
public class TransactionController {
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/api/parkingLotUsage")
    public ResponseEntity<Transaction> getUnpaidParkingLotUsageDao() {
        Transaction unpaidParkingLotUasgeData = transactionService.getUnpaidParkingLotUsageService();
        ResponseEntity<Transaction> response = ResponseEntity.status(HttpStatus.OK).body(unpaidParkingLotUasgeData);
        return response;
    }
    

    @PostMapping("/api/parkingLotUsage")
    public ResponseEntity<String> postParkingLotUsingController (@RequestBody Transaction transaction) {
        transactionService.postParkingLotUsageService(transaction);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");
        
        return response;
    }

    @PutMapping("/api/parkingLotUsage/{orderNumber}")
    public ResponseEntity<String> putParkingLotUsingController(@PathVariable String orderNumber, @RequestBody Transaction transaction) {
        transactionService.putParkingLotUsageService(orderNumber, transaction);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");
        return response;
    }
    
    @GetMapping("/api/transactionRecords")
    public ResponseEntity<List<Transaction>> getTransactionRecordsController() {
        List<Transaction> transactionRecords = transactionService.getTransactionRecordsService();
        ResponseEntity<List<Transaction>> response = ResponseEntity.status(HttpStatus.OK).body(transactionRecords);
        return response;
    }
    
}
