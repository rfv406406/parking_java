package com.sideproject.parking_java.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.model.Car;
import com.sideproject.parking_java.service.CarRegisterService;



@RestController
public class CarRegisterController {
    @Autowired
    private CarRegisterService carRegisterService;

    @GetMapping("/api/car")
    public ResponseEntity<List<Car>> getCarRegisterDataController() {
        List<Car> car = carRegisterService.getCarRegisterDataService();
        ResponseEntity<List<Car>> response = ResponseEntity.status(HttpStatus.OK).body(car);
        return response;
    }
    

    @PostMapping("/api/car")
    public ResponseEntity<String> postCarRegisterController(@ModelAttribute Car car) throws IOException {
        carRegisterService.postCarRegisterService(car);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");
        return response;
    }

    @DeleteMapping("/api/car/{carId}")
    public ResponseEntity<String> deleteCarRegisterDataController(@PathVariable Integer carId) {
        carRegisterService.deleteCarRegisterDataService(carId);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");
        return response;
    }
}
