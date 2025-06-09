package com.sideproject.parking_java.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.utility.S3Util;

@Component
public class ParkingLotImagesDao {

    @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired JdbcTemplate jdbcTemplate;

    @Autowired S3Util s3Util;

    public List<Object[]> postParkinglotimagesDao(ParkingLot parkingLot, int parkingLotDataId) {
        String sql = "INSERT INTO parkinglotimage(parkinglot_id, image) VALUES (?, ?)";
        List<Object[]> batch = new ArrayList<>();
        for (MultipartFile img : parkingLot.getImg()) {
            Object[] value = new Object[2];
            String imgUrl = s3Util.uploadToS3(img);
            value[0] = parkingLotDataId;
            value[1] = imgUrl;
            batch.add(value);  
        }
        jdbcTemplate.batchUpdate(sql, batch);

        return batch;
    }

    public List<Object[]> putParkinglotimagesDao(ParkingLot parkingLot) {
        String sqlD = "DELETE FROM parkinglotimage WHERE parkinglot_id = :parkinglot_id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("parkinglot_id", parkingLot.getParkingLotId());
        namedParameterJdbcTemplate.update(sqlD, map);
        int parkingLotDataId = parkingLot.getParkingLotId();

        String sqlI = "INSERT INTO parkinglotimage(parkinglot_id, image) VALUES (?, ?)";
        List<Object[]> batch = new ArrayList<>();
		for (MultipartFile img : parkingLot.getImg()) {
            Object[] value = new Object[2];
            String imgUrl = s3Util.uploadToS3(img);
            value[0] = parkingLotDataId;
            value[1] = imgUrl;
            batch.add(value);  
		}
        jdbcTemplate.batchUpdate(sqlI, batch);

        return batch;
    }
}
