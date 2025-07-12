package com.sideproject.parking_java.utility;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.sideproject.parking_java.model.Car;

public class CarRowMapper implements RowMapper<Car>{
    @Override
    public Car mapRow(@org.springframework.lang.NonNull ResultSet rs, int rowNum) throws SQLException{
        ResultSetMetaData metaData = rs.getMetaData();
        int length = metaData.getColumnCount();
        boolean hasImages = false;
        for (int i=1; i<=length; i++) {
            if (metaData.getColumnName(i).equals("images")) {
                hasImages = true;
            }
        }

        Car car = new Car();
        car.setId(rs.getInt("id"));
        car.setCarNumber(rs.getString("carboard_number"));
        if (hasImages) {
            car.setCarImageUrl(rs.getString("images"));
        }
        
        return car;
    }
}
