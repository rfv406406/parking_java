package com.sideproject.parking_java.dao;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.utility.ParkingLotRowMapper;

@Component
public class ParkingLotRegisterDao {

    @Autowired 
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<ParkingLot> getParingLotRegisterDao(Integer memberId, Integer parkingLotId) {
        String sql = "SELECT parkinglotdata.*, member.*, " +
             "GROUP_CONCAT(DISTINCT parkinglotimage.image SEPARATOR ',') AS images, " +
             "GROUP_CONCAT(DISTINCT CONCAT(parkinglotsquare.id,',', parkinglotsquare.parkinglot_id,',', parkinglotsquare.square_number,',', parkinglotsquare.status) " +
             "ORDER BY parkinglotsquare.square_number SEPARATOR ',') AS squares " +
             "FROM parkinglotdata " +
             "LEFT JOIN member ON parkinglotdata.member_id = member.id " +
             "LEFT JOIN parkinglotimage ON parkinglotdata.id = parkinglotimage.parkinglot_id " +
             "LEFT JOIN parkinglotsquare ON parkinglotdata.id = parkinglotsquare.parkinglot_id ";
        String sql2 = "WHERE parkinglotdata.member_id = :member_id ";
        String sql3 = "AND parkinglotdata.id = :id ";
        String sql4 = "GROUP BY parkinglotdata.id";

        HashMap<String, Object> map = new HashMap<>();
        List<ParkingLot> parkingLotList;
        if (memberId == null) {
            parkingLotList = namedParameterJdbcTemplate.query(sql+sql4, map, new ParkingLotRowMapper());
        } else if (parkingLotId != null) {
            map.put("member_id", memberId);
            map.put("id", parkingLotId);
            parkingLotList = namedParameterJdbcTemplate.query(sql+sql2+sql3+sql4, map, new ParkingLotRowMapper());
        } else {
            map.put("member_id", memberId);
            parkingLotList = namedParameterJdbcTemplate.query(sql+sql2+sql4, map, new ParkingLotRowMapper());
        }
        System.out.println("parkingLotList: "+ parkingLotList);

        return parkingLotList;
    }

    public int postParkingLotRegisterDao(ParkingLot parkingLot, int memberId) throws DatabaseError {
        String sql = "INSERT INTO parkinglotdata(member_id, name, address, landmark, opening_time, closing_time, space_in_out, price, width_limit, height_limit, lat, lng)" 
        + "VALUES (:member_id, :name, :address, :landmark, :opening_time, :closing_time, :space_in_out, :price, :width_limit, :height_limit, :lat, :lng)";
        
        KeyHolder keyHolder = new GeneratedKeyHolder();
        HashMap<String, Object> map = new HashMap<>();

        map.put("member_id", memberId);
        map.put("name", parkingLot.getName());
        map.put("address", parkingLot.getAddress());
        map.put("landmark", parkingLot.getNearLandmark());
        map.put("opening_time", parkingLot.getOpeningTime());
        map.put("closing_time", parkingLot.getClosingTime());
        map.put("space_in_out", parkingLot.getSpaceInOut());
        map.put("price", parkingLot.getPrice());
        map.put("width_limit", parkingLot.getCarWidth());
        map.put("height_limit", parkingLot.getCarHeight());
        map.put("lat", parkingLot.getLatitude());
        map.put("lng", parkingLot.getLongitude());
        
        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);
        Number key = keyHolder.getKey();

        if (key == null) {
            throw new DatabaseError("No key returned");
        }

        int parkingLotId = key.intValue();
    
        return parkingLotId;
    }
    
    public int deleteParkingLotRegisterDao(Integer parkingLot, int memberId) {
        String sql1 = "DELETE FROM parkingsquareimage WHERE parkinglotsquare_id IN (SELECT id FROM parkinglotsquare WHERE parkinglot_id = :parkinglot_id)";
        String sql2 = "DELETE FROM parkinglotsquare WHERE parkinglot_id = :parkinglot_id";
        String sql3 = "DELETE FROM parkinglotimage WHERE parkinglot_id = :parkinglot_id";
        String sql4 = "DELETE FROM parkinglotdata WHERE id = :parkinglot_id AND member_id = :member_id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("parkinglot_id", parkingLot);
        map.put("member_id", memberId);
        namedParameterJdbcTemplate.update(sql1, map);
        namedParameterJdbcTemplate.update(sql2, map);
        namedParameterJdbcTemplate.update(sql3, map);
        int insertId = namedParameterJdbcTemplate.update(sql4, map);

        return insertId;
    }

    public int putParkingLotRegisterDao(ParkingLot parkingLot, int memberId) {
        String sql = "UPDATE parkinglotdata SET " +
        "name = :name, " +
        "address = :address, " +
        "landmark = :landmark, " +
        "opening_time = :opening_time, " +
        "closing_time = :closing_time, " +
        "space_in_out = :space_in_out, " +
        "price = :price, " +
        "width_limit = :width_limit, " +
        "height_limit = :height_limit, " +
        "lng = :lng, " +
        "lat = :lat " +
        "WHERE id = :parkinglot_id AND member_id = :member_id";
    
        HashMap<String, Object> map = new HashMap<>();
        map.put("parkinglot_id", parkingLot.getParkingLotId());
        map.put("member_id", memberId);
        map.put("name", parkingLot.getName());
        map.put("address", parkingLot.getAddress());
        map.put("landmark", parkingLot.getNearLandmark());
        map.put("opening_time", parkingLot.getOpeningTime());
        map.put("closing_time", parkingLot.getClosingTime());
        map.put("space_in_out", parkingLot.getSpaceInOut());
        map.put("price", parkingLot.getPrice());
        map.put("width_limit", parkingLot.getCarWidth());
        map.put("height_limit", parkingLot.getCarHeight());
        map.put("lng", parkingLot.getLongitude());
        map.put("lat", parkingLot.getLatitude());

        int insertId = namedParameterJdbcTemplate.update(sql, map);
        return insertId;
    }
}
