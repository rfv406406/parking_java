package com.sideproject.parking_java.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sideproject.parking_java.Dao.ParkingLotRegisterDao;
import com.sideproject.parking_java.Exception.DatabaseError;
import com.sideproject.parking_java.Exception.InvalidParameterError;
import com.sideproject.parking_java.Model.ParkingLot;

@Service
public class ParkingLotRegisterService{
    @Autowired
    private ParkingLotRegisterDao parkingLotRegisterDao;

    public int postParkingLotRegister(ParkingLot parkingLot) throws DatabaseError {
        if (parkingLot.getName() == null || parkingLot.getName().equals("") ||
            parkingLot.getAddress() == null || parkingLot.getAddress().equals("") ||
            parkingLot.getNearLandmark() == null || parkingLot.getNearLandmark().equals("") ||
            parkingLot.getOpeningTimeAm() == null || parkingLot.getOpeningTimeAm().equals("") ||
            parkingLot.getOpeningTimePm() == null || parkingLot.getOpeningTimePm().equals("") ||
            parkingLot.getSpaceInOut() == null || parkingLot.getSpaceInOut().equals("") ||
            parkingLot.getCarWidth() == null || parkingLot.getCarWidth().equals("") ||
            parkingLot.getCarHeight() == null || parkingLot.getCarHeight().equals("") ||
            parkingLot.getLatitude() == null || parkingLot.getLatitude().equals("") ||
            parkingLot.getLongitude() == null || parkingLot.getLongitude().equals("") ||
            parkingLot.getCarSpaceNumber() == null || parkingLot.getCarSpaceNumber().isEmpty() 
            ) {
            throw new InvalidParameterError("parameter is null or empty");
        }
        int insertId = parkingLotRegisterDao.postParkingLotRegisterDao(parkingLot);
        if (insertId == 0) {
            throw new DatabaseError("insert failed");
        }
        return insertId;
    }
}
