package com.sideproject.parking_java.Dao;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.Model.CarSpaceNumber;
import com.sideproject.parking_java.Model.ParkingLot;

@Component
public class ParkingLotSquareDao {
    
    @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int inputParkingLotSquare(ParkingLot parkingLot, int parkingLotDataId) {
        int insertId = 0;
        for (CarSpaceNumber carSpaceNumber : parkingLot.getCarSpaceNumber()) {
            String sql = "INSERT INTO parkinglotsquare(parkinglotdata_id, square_number, status) VALUES(:parkinglotdata_id, :square_number, :status)";
            HashMap<String, Object> map = new HashMap<>();
            map.put("parkinglotdata_id", parkingLotDataId);
            map.put("square_number", carSpaceNumber.getValue());
            map.put("status", "閒置中");
            int insertCount = namedParameterJdbcTemplate.update(sql, map);
            insertId += insertCount;
        }
        return insertId;
    }
}
