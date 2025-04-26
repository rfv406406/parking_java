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

    public int putParkinglotsquareDao(ParkingLot parkingLot) {
        int insertNumber = 0;
        for (CarSpaceNumber carSpaceNumber : parkingLot.getCarSpaceNumber()) {
            HashMap<String, Object> map = new HashMap<>();
            if (carSpaceNumber.getId() == null) {
                String sqlI = "INSERT INTO parkinglotsquare(parkinglot_id, square_number, status) VALUES(:parkinglot_id, :square_number, :status)";
                map.put("parkinglot_id", parkingLot.getParkingLotId());
                map.put("square_number", carSpaceNumber.getValue());
                map.put("status", "閒置中");
                int updateCount = namedParameterJdbcTemplate.update(sqlI, map);
                insertNumber += updateCount;
            } else {
                String sqlU = "UPDATE parkinglotsquare SET square_number = :square_number " +
                "WHERE id = :id AND parkinglot_id = :parkinglot_id";            
                map.put("parkinglot_id", parkingLot.getParkingLotId());
                map.put("square_number", carSpaceNumber.getValue());
                map.put("id", carSpaceNumber.getId());
                int insertCount = namedParameterJdbcTemplate.update(sqlU, map);
                insertNumber += insertCount;
            }
        }
        return insertNumber;
    }
}
