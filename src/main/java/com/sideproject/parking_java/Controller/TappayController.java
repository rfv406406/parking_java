package com.sideproject.parking_java.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.model.Tappay;
import com.sideproject.parking_java.service.TappayService;


@RestController
public class TappayController {
    @Autowired 
    private TappayService tappayService;

    @PostMapping("/api/tappay")
    public ResponseEntity<String> postTappayController(@RequestBody Tappay tappay) throws RestClientException, DatabaseError {
        String tappayResponse = tappayService.postTappayService(tappay);

        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body(tappayResponse);
        
        return response;
    } 
}
