package com.sideproject.parking_java.dao;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.model.Car;

@Component
public class CarRegisterDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void postInsertCar(int memberId, Car car) {
        String sql = "INSERT INTO car(member_id, carboard_number) VALUES(:member_id, :carboard_number)";
        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
        map.put("carboard_number", car.getCarNumber());

        namedParameterJdbcTemplate.update(sql, map);
    }

    public void postInsertCarImage(int memberId, Car car) {
        String fileName = car.getCarImage().getOriginalFilename();

        if(fileName != null && (fileName.endsWith("jpg") || fileName.endsWith("jpeg") ||
        fileName.endsWith("png") || fileName.endsWith("jfif"))) {
            String UniquefileName = UUID.randomUUID().toString() + fileName;
            // s3_client.upload_fileobj(image, BUCKET_NAME, filename)
            String imgUrl = "https://d1hxt3hn1q2xo2.cloudfront.net/" + UniquefileName;
            String sql = "INSERT INTO car_image(car_id, car_image) VALUES(:car_id, :car_image)";
            HashMap<String, Object> map = new HashMap<>();
            map.put("car_id", memberId);
            map.put("car_image", imgUrl);
    
            namedParameterJdbcTemplate.update(sql, map);
        }
       
    }
}
