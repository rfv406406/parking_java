package com.sideproject.parking_java.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.sideproject.parking_java.model.Car;

public class CarRowMapper implements RowMapper<Car>{
    @Override
    public Car mapRow(@org.springframework.lang.NonNull ResultSet rs, int rowNum) throws SQLException{
        Car car = new Car();
        car.setId(rs.getInt("id"));
        car.setCarNumber(rs.getString("carboard_number"));
        car.setCarImageUrl(rs.getString("images"));
        return car;
    }
}
