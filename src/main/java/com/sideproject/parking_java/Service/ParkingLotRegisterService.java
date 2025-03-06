package com.sideproject.parking_java.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.Dao.ParkingLotRegisterDao;
import com.sideproject.parking_java.Model.ParkingLot;

@Service
public class ParkingLotRegisterService {
    @Autowired
    private ParkingLotRegisterDao parkingLotRegisterDao;

    public int postParkingLotRegister(ParkingLot parkingLot) {
        return parkingLotRegisterDao.postParkingLotRegisterDao(parkingLot);
    }
}
