package com.sideproject.parking_java.dao;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.utility.ParkingLotRowMapper;

import jakarta.transaction.Transactional;

@Component
public class ParkingLotRegisterDao {

    @Autowired 
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<ParkingLot> getParingLotRegisterDao() {
        String sql = "SELECT parkinglotdata.*, " +
             "GROUP_CONCAT(parkinglotimage.image SEPARATOR ',') AS images, " +
             "GROUP_CONCAT(CONCAT(parkinglotsquare.parkinglot_id, ',', parkinglotsquare.square_number, ',', parkinglotsquare.status) " +
             "ORDER BY parkinglotsquare.square_number SEPARATOR ',') AS squares " +
             "FROM parkinglotdata " +
             "LEFT JOIN parkinglotimage ON parkinglotdata.id = parkinglotimage.parkinglot_id " +
             "LEFT JOIN parkinglotsquare ON parkinglotdata.id = parkinglotsquare.parkinglot_id " +
             "GROUP BY parkinglotdata.id";

        HashMap<String, Object> map = new HashMap<>();
        List<ParkingLot> parkingLot = namedParameterJdbcTemplate.query(sql, map, new ParkingLotRowMapper());

        for (ParkingLot p : parkingLot) {
            System.out.println(p);
        }
      
        return parkingLot;
    }

    public int postParkingLotRegisterDao(ParkingLot parkingLot, int memberId) {
        String sql = "INSERT INTO parkinglotdata(member_id, name, address, landmark, opening_time, closing_time, space_in_out, price, width_limit, height_limit, lat, lng)" 
        + "VALUES (:member_id, :name, :address, :landmark, :opening_time, :closing_time, :space_in_out, :price, :width_limit, :height_limit, :lat, :lng)";

        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
        map.put("name", parkingLot.getName());
        map.put("address", parkingLot.getAddress());
        map.put("landmark", parkingLot.getNearLandmark());
        map.put("opening_time", parkingLot.getOpeningTimeAm());
        map.put("closing_time", parkingLot.getOpeningTimePm());
        map.put("space_in_out", parkingLot.getSpaceInOut());
        map.put("price", parkingLot.getPrice());
        map.put("width_limit", parkingLot.getCarWidth());
        map.put("height_limit", parkingLot.getCarHeight());
        map.put("lat", parkingLot.getLatitude());
        map.put("lng", parkingLot.getLongitude());
        
        int insertId = namedParameterJdbcTemplate.update(sql, map);   

        return insertId;
    }
    
    @Transactional
    public int deleteParkingLotRegisterDao(ParkingLot parkingLot, int memberId) {
        String sql1 = "DELETE FROM parkingsquareimage WHERE parkinglotsquare_id IN (SELECT id FROM parkinglotsquare WHERE parkinglot_id = :parkinglot_id)";
        String sql2 = "DELETE FROM parkinglotsquare WHERE parkinglot_id = :parkinglot_id";
        String sql3 = "DELETE FROM parkinglotimage WHERE parkinglot_id = :parkinglot_id";
        String sql4 = "DELETE FROM parkinglotdata WHERE id = :parkinglot_id AND member_id = :member_id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("parkinglot_id", parkingLot.getParkingLotId());
        map.put("member_id", memberId);
        namedParameterJdbcTemplate.update(sql1, map);
        namedParameterJdbcTemplate.update(sql2, map);
        namedParameterJdbcTemplate.update(sql3, map);
        int insertId = namedParameterJdbcTemplate.update(sql4, map);

        return insertId;
    }

    public int putParkingLotRegisterDao(ParkingLot parkingLot, int memberId) {
        String sql = "UPDATE parkinglotdata SET name, address, landmark, opening_time, closing_time, space_in_out, price, width_limit, height_limit, ing, lat"
        + "WHERE id = :parkinglot_id AND mmeber_id = :member_id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("parkinglot_id", parkingLot.getParkingLotId());
        map.put("member_id", memberId);
        int insertId = namedParameterJdbcTemplate.update(sql, map);
        return insertId;
    }
}
