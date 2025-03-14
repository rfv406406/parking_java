package com.sideproject.parking_java.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.model.Car;


@RestController
public class CarController {

    @PostMapping("path")
    public ResponseEntity<String> postCar(@RequestBody Car car) {
        //TODO: process POST request
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");
        return response;
    }
}
