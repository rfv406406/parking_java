package com.sideproject.parking_java.dao;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.model.ParkingLot;

@Component
public class ParkingLotRegisterDao {

    @Autowired 
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int postParkingLotRegisterDao(ParkingLot parkingLot, int memberId) {
        String sql = "INSERT INTO parkinglotdata(member_id, name, address, landmark, openingTime, closingTime, spaceInOut, price, widthLimit, heightLimit, lat, lng)" 
        + "VALUES (:member_id, :name, :address, :landmark, :openingTime, :closingTime, :spaceInOut, :price, :widthLimit, :heightLimit, :lat, :lng)";

        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
        map.put("name", parkingLot.getName());
        map.put("address", parkingLot.getAddress());
        map.put("landmark", parkingLot.getNearLandmark());
        map.put("openingTime", parkingLot.getOpeningTimeAm());
        map.put("closingTime", parkingLot.getOpeningTimePm());
        map.put("spaceInOut", parkingLot.getSpaceInOut());
        map.put("price", parkingLot.getPrice());
        map.put("widthLimit", parkingLot.getCarWidth());
        map.put("heightLimit", parkingLot.getCarHeight());
        map.put("lat", parkingLot.getLatitude());
        map.put("lng", parkingLot.getLongitude());
        
        int insertId = namedParameterJdbcTemplate.update(sql, map);   

        return insertId;
    }

    public int deleteParkingLotRegisterDao(ParkingLot parkingLot, int memberId) {
        String sql1 = "DELETE FROM parkingsquareimage WHERE parkinglotsquare_id IN (SELECT id FROM parkinglotsquare WHERE parkinglotdata_id = :parkinglotdata_id)";
        String sql2 = "DELETE FROM parkinglotsquare WHERE parkinglotdata_id = :parkinglotdata_id";
        String sql3 = "DELETE FROM parkinglotimage WHERE parkinglotdata_id = :parkinglotdata_id";
        String sql4 = "DELETE FROM parkinglotdata WHERE id = :parkinglotdata_id AND member_id = :member_id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("parkinglotdata_id", parkingLot.getParkingLotId());
        map.put("member_id", memberId);
        namedParameterJdbcTemplate.update(sql1, map);
        namedParameterJdbcTemplate.update(sql2, map);
        namedParameterJdbcTemplate.update(sql3, map);
        int insertId = namedParameterJdbcTemplate.update(sql4, map);

        return insertId;
    }

    public int putParkingLotRegisterDao(ParkingLot parkingLot, int memberId) {
        String sql = "UPDATE parkinglotdata SET name, address, landmark, openingTime, closingTime, spaceInOut, price, widthLimit, heightLimit, ing, lat"
        + "WHERE id = :parkinglotdata_id AND mmeber_id = :member_id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("parkinglotdata_id", parkingLot.getParkingLotId());
        map.put("member_id", memberId);
        int insertId = namedParameterJdbcTemplate.update(sql, map);
        return insertId;
    }
}
