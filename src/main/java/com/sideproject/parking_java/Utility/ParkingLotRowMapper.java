package com.sideproject.parking_java.Utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.sideproject.parking_java.Model.ParkingLot;

public class ParkingLotRowMapper implements RowMapper<ParkingLot>{
    @Override
    public ParkingLot mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException{
        ParkingLot parkinglot = new ParkingLot();
        parkinglot.setId(rs.getInt("id"));
        return parkinglot;
    }
}

