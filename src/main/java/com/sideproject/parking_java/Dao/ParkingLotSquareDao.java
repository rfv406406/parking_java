package com.sideproject.parking_java.dao;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.model.CarSpaceNumber;
import com.sideproject.parking_java.model.ParkingLot;

@Component
public class ParkingLotSquareDao {
    
    @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int postParkingLotSquareDao(ParkingLot parkingLot, int parkingLotDataId) {
        int insertId = 0;
        for (CarSpaceNumber carSpaceNumber : parkingLot.getCarSpaceNumber()) {
            String sql = "INSERT INTO parkinglotsquare(parkinglot_id, square_number, status) VALUES(:parkinglot_id, :square_number, :status)";
            HashMap<String, Object> map = new HashMap<>();
            map.put("parkinglot_id", parkingLotDataId);
            map.put("square_number", carSpaceNumber.getValue());
            map.put("status", "閒置中");
            int insertCount = namedParameterJdbcTemplate.update(sql, map);
            insertId += insertCount;
        }
        return insertId;
    }

    public int putParkinglotsquareDao(ParkingLot parkingLot, int parkingLotDataId) {
        int insertId = 0;
        for (CarSpaceNumber carSpaceNumber : parkingLot.getCarSpaceNumber()) {
            String sql = "UPDATE parkinglotsquare SET square_number = :square_number" +
            "WHERE parkinglot_id = :parkinglot_id";            
            HashMap<String, Object> map = new HashMap<>();
            map.put("parkinglot_id", parkingLotDataId);
            map.put("square_number", carSpaceNumber.getValue());
            int insertCount = namedParameterJdbcTemplate.update(sql, map);
            insertId += insertCount;
        }
        return insertId;
    }
}
