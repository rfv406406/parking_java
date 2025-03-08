package com.sideproject.parking_java.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.sideproject.parking_java.Model.ParkingLot;

public class ParkingLotRowMapper implements RowMapper<ParkingLot>{
    @Override
    public ParkingLot mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException{
        ParkingLot parkinglot = new ParkingLot();
        parkinglot.setParkingLotId(rs.getInt("id"));
        return parkinglot;
    }
}

