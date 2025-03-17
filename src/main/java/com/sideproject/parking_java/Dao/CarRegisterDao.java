package com.sideproject.parking_java.dao;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.model.Car;
import com.sideproject.parking_java.utility.CarRowMapper;

@Component
public class CarRegisterDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public List<Car> getCarRegisterDataDao(int memberId) {
        String sql = "SELECT car.*, GROUP_CONCAT(ci.image SEPARATOR ',') AS images FROM car " +
                     "LEFT JOIN car_image ci ON car.id = ci.car_id WHERE member_id = :member_id GROUP BY car.id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);

        List<Car> car = namedParameterJdbcTemplate.query(sql, map, new CarRowMapper());

        return car;
    }

    public int getCarIdDao(int memberId) {
        String sql = "SELECT id, carboard_number FROM car WHERE member_id = :member_id ORDER BY id DESC LIMIT 1";
        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
        Car carId = namedParameterJdbcTemplate.queryForObject(sql, map, new CarRowMapper());
        return carId.getId();
    }

    public void postInsertCarDao(int memberId, Car car) {
        String sql = "INSERT INTO car(member_id, carboard_number) VALUES(:member_id, :carboard_number)";
        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
        map.put("carboard_number", car.getCarNumber());

        namedParameterJdbcTemplate.update(sql, map);
    }

    public void postInsertCarImageDao(int carId, Car car) {
        String fileName = car.getCarImage().getOriginalFilename();

        if(fileName != null && (fileName.endsWith("jpg") || fileName.endsWith("jpeg") ||
        fileName.endsWith("png") || fileName.endsWith("jfif"))) {
            String UniquefileName = UUID.randomUUID().toString() + fileName;
            // s3_client.upload_fileobj(image, BUCKET_NAME, filename)
            String imgUrl = "https://d1hxt3hn1q2xo2.cloudfront.net/" + UniquefileName;
            String sql = "INSERT INTO car_image(car_id, image) VALUES(:car_id, :image)";
            HashMap<String, Object> map = new HashMap<>();
            map.put("car_id", carId);
            map.put("image", imgUrl);
    
            namedParameterJdbcTemplate.update(sql, map);
        }
       
    }

    public void deleteCarDao(int carId, int memberId) {
        String sql = "DELETE FROM car WHERE id = :id AND member_id = :member_id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("id", carId);
        map.put("member_id", memberId);
        namedParameterJdbcTemplate.update(sql, map);
    }

    public void deleteCarImageDao(int carId) {
        String sql = "DELETE FROM car_image WHERE car_id = :car_id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("car_id", carId);
        namedParameterJdbcTemplate.update(sql, map);
    }
}
