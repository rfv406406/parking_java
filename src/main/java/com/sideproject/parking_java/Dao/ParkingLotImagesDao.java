package com.sideproject.parking_java.dao;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.sideproject.parking_java.model.ParkingLot;

@Component
public class ParkingLotImagesDao {

    @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int postParkinglotimagesDao(ParkingLot parkingLot, int parkingLotDataId) throws RuntimeException{
        int count = 0;
        for (MultipartFile img : parkingLot.getImg()) {
            try {
                String fileName = img.getOriginalFilename();
                if(fileName != null && (fileName.endsWith("jpg") || fileName.endsWith("jpeg") ||
                fileName.endsWith("png") || fileName.endsWith("jfif"))) {
                    String UniquefileName = UUID.randomUUID().toString() + fileName;
                    // s3_client.upload_fileobj(image, BUCKET_NAME, filename)
                    String imgUrl = "https://d1hxt3hn1q2xo2.cloudfront.net/" + UniquefileName;
                    String sql = "INSERT INTO parkinglotimage(parkinglot_id, image) VALUES (:parkinglot_id, :image)";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("parkinglot_id", parkingLotDataId);
                    map.put("image", imgUrl);
                    int insertId = namedParameterJdbcTemplate.update(sql, map);
                    count = count + insertId;
                }
            } catch(RuntimeException e) {
                System.out.println(e);
            }
        }
        return count;
    }

    public int putParkinglotimagesDao(ParkingLot parkingLot, int parkingLotDataId) {
        int count = 0;
        for (MultipartFile img : parkingLot.getImg()) {
            try {
                String fileName = img.getOriginalFilename();
                if(fileName != null && (fileName.endsWith("jpg") || fileName.endsWith("jpeg") ||
                fileName.endsWith("png") || fileName.endsWith("jfif"))) {
                    String UniquefileName = UUID.randomUUID().toString() + fileName;
                    // s3_client.upload_fileobj(image, BUCKET_NAME, filename)
                    String imgUrl = "https://d1hxt3hn1q2xo2.cloudfront.net/" + UniquefileName;
                    String sql = "UPDATE parkinglotimage SET image = :image WHERE parkinglot_id = :parkinglot_id";
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("parkinglot_id", parkingLotDataId);
                    map.put("image", imgUrl);
                    int insertId = namedParameterJdbcTemplate.update(sql, map);
                    count = count + insertId;
                }
            } catch(RuntimeException e) {
                System.out.println(e);
            }
        }
        return count;
    }
}
