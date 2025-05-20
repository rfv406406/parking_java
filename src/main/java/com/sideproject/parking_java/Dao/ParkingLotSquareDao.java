package com.sideproject.parking_java.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.model.CarSpaceNumber;
import com.sideproject.parking_java.model.ParkingLot;

@Component
public class ParkingLotSquareDao {
    
    @Autowired 
    JdbcTemplate jdbcTemplate;

    public List<Object[]> postParkingLotSquareDao(ParkingLot parkingLot, int parkingLotDataId) {
        // int insertId = 0;
        String sql = "INSERT INTO parkinglotsquare(parkinglot_id, square_number, status) VALUES(?, ?, ?)";
        List<Object[]> batch = new ArrayList<>();
        for (CarSpaceNumber carSpaceNumber : parkingLot.getCarSpaceNumber()) {
            Object[] value = new Object[] {
                parkingLotDataId, carSpaceNumber.getValue(), "閒置中"
            };
            batch.add(value);
        }
        jdbcTemplate.batchUpdate(sql, batch);
        // for (CarSpaceNumber carSpaceNumber : parkingLot.getCarSpaceNumber()) {
        //     String sql = "INSERT INTO parkinglotsquare(parkinglot_id, square_number, status) VALUES(:parkinglot_id, :square_number, :status)";
        //     HashMap<String, Object> map = new HashMap<>();
        //     map.put("parkinglot_id", parkingLotDataId);
        //     map.put("square_number", carSpaceNumber.getValue());
        //     map.put("status", "閒置中");
        //     int insertCount = namedParameterJdbcTemplate.update(sql, map);
        //     insertId += insertCount;
        // }
        return batch;
    }

    public int putParkinglotsquareDao(ParkingLot parkingLot) {
        int count = 0;
        String sqlI = "INSERT INTO parkinglotsquare(parkinglot_id, square_number, status) VALUES(?, ?, ?)";
        String sqlU = "UPDATE parkinglotsquare SET square_number = ? WHERE id = ? AND parkinglot_id = ?";            
        List<Object[]> batchI = new ArrayList<>();
        List<Object[]> batchU = new ArrayList<>();

        for (CarSpaceNumber carSpaceNumber : parkingLot.getCarSpaceNumber()) {
            if (carSpaceNumber.getId() == null) {
                Object[] value = new Object[] {
                    parkingLot.getParkingLotId(), carSpaceNumber.getValue(), "閒置中"
                };
                batchI.add(value);
            } else {
                Object[] value = new Object[] {
                    carSpaceNumber.getValue(), carSpaceNumber.getId(), parkingLot.getParkingLotId()
                };
                batchU.add(value);
            }
        }
        if (!batchI.isEmpty()) {
            jdbcTemplate.batchUpdate(sqlI, batchI);
            count += batchI.size();
        }
        if (!batchU.isEmpty()) {
            jdbcTemplate.batchUpdate(sqlU, batchU);
            count += batchU.size();
        }

        return count;
        // for (CarSpaceNumber carSpaceNumber : parkingLot.getCarSpaceNumber()) {
        //     HashMap<String, Object> map = new HashMap<>();
        //     if (carSpaceNumber.getId() == null) {
        //         String sqlI = "INSERT INTO parkinglotsquare(parkinglot_id, square_number, status) VALUES(:parkinglot_id, :square_number, :status)";
        //         map.put("parkinglot_id", parkingLot.getParkingLotId());
        //         map.put("square_number", carSpaceNumber.getValue());
        //         map.put("status", "閒置中");
        //         int updateCount = namedParameterJdbcTemplate.update(sqlI, map);
        //         insertNumber += updateCount;
        //     } else {
        //         String sqlU = "UPDATE parkinglotsquare SET square_number = :square_number " +
        //         "WHERE id = :id AND parkinglot_id = :parkinglot_id";            
        //         map.put("parkinglot_id", parkingLot.getParkingLotId());
        //         map.put("square_number", carSpaceNumber.getValue());
        //         map.put("id", carSpaceNumber.getId());
        //         int insertCount = namedParameterJdbcTemplate.update(sqlU, map);
        //         insertNumber += insertCount;
        //     }
        // }
        // return insertNumber;
    }
}
