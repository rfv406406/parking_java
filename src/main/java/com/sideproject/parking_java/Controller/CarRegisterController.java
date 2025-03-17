package com.sideproject.parking_java.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sideproject.parking_java.model.Car;
import com.sideproject.parking_java.service.CarRegisterService;



@RestController
public class CarRegisterController {
    @Autowired
    private CarRegisterService carRegisterService;

    @GetMapping("/api/carRegister")
    public ResponseEntity<List<Car>> getCarRegisterDataController() {
        List<Car> car = carRegisterService.getCarRegisterDataService();
        ResponseEntity<List<Car>> response = ResponseEntity.status(HttpStatus.OK).body(car);
        return response;
    }
    

    @PostMapping("/api/carRegister")
    public ResponseEntity<String> postCarRegisterController(@ModelAttribute Car car) {
        carRegisterService.postCarRegisterService(car);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");
        return response;
    }

    @DeleteMapping("/api/carRegister")
    public ResponseEntity<String> deleteCarRegisterDataController(@RequestBody Car car) {
        carRegisterService.deleteCarRegisterDataService(car);
        ResponseEntity<String> response = ResponseEntity.status(HttpStatus.OK).body("OK");
        return response;
    }
}
