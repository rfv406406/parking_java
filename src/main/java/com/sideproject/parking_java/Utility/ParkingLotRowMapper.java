package com.sideproject.parking_java.utility;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.sideproject.parking_java.model.CarSpaceNumber;
import com.sideproject.parking_java.model.ParkingLot;

public class ParkingLotRowMapper implements RowMapper<ParkingLot>{
    @Override
    public ParkingLot mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException{
        ParkingLot parkinglot = new ParkingLot();
        parkinglot.setParkingLotId(rs.getInt("id"));
        parkinglot.setName(rs.getString("name"));
        parkinglot.setAddress(rs.getString("address"));
        parkinglot.setNearLandmark(rs.getString("landmark"));
        parkinglot.setOpeningTimeAm(rs.getString("openingTime"));
        parkinglot.setOpeningTimePm(rs.getString("closingTime"));
        parkinglot.setSpaceInOut(rs.getString("spaceInOut"));
        parkinglot.setPrice(rs.getInt("price"));
        parkinglot.setCarWidth(rs.getString("widthLimit"));
        parkinglot.setCarHeight(rs.getString("heightLimit"));
        parkinglot.setLatitude(rs.getString("lat"));
        parkinglot.setLongitude(rs.getString("lng"));

        String imgUrlConcat = rs.getString("images");
        if (imgUrlConcat != null && !imgUrlConcat.isEmpty()) {
            String[] imgUrl = imgUrlConcat.split(",");
            parkinglot.setImgUrl(imgUrl);
        }

        String carSpaceNumberConcatString = rs.getString("squares");
        if (carSpaceNumberConcatString != null && !carSpaceNumberConcatString.isEmpty()) {

            String[] carSpaceNumberConcatToStringArray = carSpaceNumberConcatString.split(",");
            ArrayList<CarSpaceNumber> carSpaceNumberConcat = new ArrayList<>();

            for (int i=0; i<carSpaceNumberConcatToStringArray.length; i=i+3) {
                CarSpaceNumber carSpaceNumber = new CarSpaceNumber();
                carSpaceNumber.setParkingLotId(Integer.parseInt(carSpaceNumberConcatToStringArray[i]));
                carSpaceNumber.setValue(carSpaceNumberConcatToStringArray[i+1]);
                carSpaceNumber.setStatus(carSpaceNumberConcatToStringArray[i+2]);
                carSpaceNumberConcat.add(carSpaceNumber);
            }

            parkinglot.setCarSpaceNumber(carSpaceNumberConcat);
        }
        
        return parkinglot;
    }
}

