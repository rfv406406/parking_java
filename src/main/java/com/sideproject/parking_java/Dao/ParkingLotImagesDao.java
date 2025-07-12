package com.sideproject.parking_java.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.model.ParkingLot;

@Component
public class ParkingLotImagesDao {

    @Autowired 
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired 
    private JdbcTemplate jdbcTemplate;


    public List<Object[]> postParkinglotimagesDao(List<String> imgUrlList, int parkingLotDataId) {
        String sql = "INSERT INTO parkinglotimage(parkinglot_id, image) VALUES (?, ?)";
        List<Object[]> batch = new ArrayList<>();
        for (String url : imgUrlList) {
            Object[] value = new Object[2];
            value[0] = parkingLotDataId;
            value[1] = url;
            batch.add(value);  
        }
        jdbcTemplate.batchUpdate(sql, batch);

        return batch;
    }

    public List<Object[]> putParkinglotimagesDao(ParkingLot parkingLot, List<String> imgUrlList) {
        String sqlD = "DELETE FROM parkinglotimage WHERE parkinglot_id = :parkinglot_id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("parkinglot_id", parkingLot.getParkingLotId());
        namedParameterJdbcTemplate.update(sqlD, map);
        int parkingLotDataId = parkingLot.getParkingLotId();

        String sqlI = "INSERT INTO parkinglotimage(parkinglot_id, image) VALUES (?, ?)";
        List<Object[]> batch = new ArrayList<>();
		for (String imgUrl : imgUrlList) {
            Object[] value = new Object[2];
            value[0] = parkingLotDataId;
            value[1] = imgUrl;
            batch.add(value);  
		}
        jdbcTemplate.batchUpdate(sqlI, batch);

        return batch;
    }
}
