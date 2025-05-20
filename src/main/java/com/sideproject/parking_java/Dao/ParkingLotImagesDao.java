package com.sideproject.parking_java.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.sideproject.parking_java.model.ParkingLot;

@Component
public class ParkingLotImagesDao {

    @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired JdbcTemplate jdbcTemplate;

    public List<Object[]> postParkinglotimagesDao(ParkingLot parkingLot, int parkingLotDataId) {
        String sql = "INSERT INTO parkinglotimage(parkinglot_id, image) VALUES (?, ?)";
        List<Object[]> batch = new ArrayList<>();
        for (MultipartFile img : parkingLot.getImg()) {
            String fileName = img.getOriginalFilename();
            if(fileName != null && (fileName.endsWith("jpg") || fileName.endsWith("jpeg") ||
                fileName.endsWith("png") || fileName.endsWith("jfif"))) {
                    String UniquefileName = UUID.randomUUID().toString() + fileName;
                    // s3_client.upload_fileobj(image, BUCKET_NAME, filename)
                    String imgUrl = "https://d1hxt3hn1q2xo2.cloudfront.net/" + UniquefileName;   
                    Object[] value = new Object[] {
                        parkingLot.getParkingLotId(), imgUrl
                    };
                    batch.add(value);             
                }
        }
        jdbcTemplate.batchUpdate(sql, batch);
        // for (MultipartFile img : parkingLot.getImg()) {
        //     try {
        //         String fileName = img.getOriginalFilename();
        //         if(fileName != null && (fileName.endsWith("jpg") || fileName.endsWith("jpeg") ||
        //         fileName.endsWith("png") || fileName.endsWith("jfif"))) {
        //             String UniquefileName = UUID.randomUUID().toString() + fileName;
        //             // s3_client.upload_fileobj(image, BUCKET_NAME, filename)
        //             String imgUrl = "https://d1hxt3hn1q2xo2.cloudfront.net/" + UniquefileName;
        //             String sql = "INSERT INTO parkinglotimage(parkinglot_id, image) VALUES (:parkinglot_id, :image)";
        //             HashMap<String, Object> map = new HashMap<>();
        //             map.put("parkinglot_id", parkingLotDataId);
        //             map.put("image", imgUrl);
        //             int insertId = namedParameterJdbcTemplate.update(sql, map);
        //             count = count + insertId;
        //         }
        //     } catch(RuntimeException e) {
        //         System.out.println(e);
        //     }
        // }
        return batch;
    }

    public List<Object[]> putParkinglotimagesDao(ParkingLot parkingLot) {
        String sqlD = "DELETE FROM parkinglotimage WHERE parkinglot_id = :parkinglot_id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("parkinglot_id", parkingLot.getParkingLotId());
        namedParameterJdbcTemplate.update(sqlD, map);

        String sqlI = "INSERT INTO parkinglotimage(parkinglot_id, image) VALUES (?, ?)";
        List<Object[]> batch = new ArrayList<>();
		for (MultipartFile img : parkingLot.getImg()) {
            String fileName = img.getOriginalFilename();
            if(fileName != null && (fileName.endsWith("jpg") || fileName.endsWith("jpeg") ||
                fileName.endsWith("png") || fileName.endsWith("jfif"))) {
                    String UniquefileName = UUID.randomUUID().toString() + fileName;
                    // s3_client.upload_fileobj(image, BUCKET_NAME, filename)
                    String imgUrl = "https://d1hxt3hn1q2xo2.cloudfront.net/" + UniquefileName;     
                    Object[] values = new Object[] {
                        parkingLot.getParkingLotId(), imgUrl
                    };
                    batch.add(values);             
                }
		}
        jdbcTemplate.batchUpdate(sqlI, batch);
        // count = count + insertNumber;


        // for (MultipartFile img : parkingLot.getImg()) {
        //     try {
        //         String fileName = img.getOriginalFilename();
        //         if(fileName != null && (fileName.endsWith("jpg") || fileName.endsWith("jpeg") ||
        //         fileName.endsWith("png") || fileName.endsWith("jfif"))) {
        //             String UniquefileName = UUID.randomUUID().toString() + fileName;
        //             // s3_client.upload_fileobj(image, BUCKET_NAME, filename)
        //             String imgUrl = "https://d1hxt3hn1q2xo2.cloudfront.net/" + UniquefileName;
        //             String sqlI = "INSERT INTO parkinglotimage(parkinglot_id, image) VALUES (:parkinglot_id, :image)";
        //             // HashMap<String, Object> map = new HashMap<>();
        //             // map.put("parkinglot_id", parkingLot.getParkingLotId());
        //             map.put("image", imgUrl);
        //             // namedParameterJdbcTemplate.update(sqlD, map);
        //             int insertNumber = namedParameterJdbcTemplate.update(sqlI, map);
        //             count = count + insertNumber;
        //         }
        //     } catch(RuntimeException e) {
        //         System.out.println(e);
        //     }
        // }
        return batch;
    }
}
