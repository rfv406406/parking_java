package com.sideproject.parking_java.utility;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.jdbc.core.RowMapper;

import com.sideproject.parking_java.model.CarSpaceNumber;
import com.sideproject.parking_java.model.ParkingLot;

public class ParkingLotRowMapper implements RowMapper<ParkingLot>{
    @Override
    public ParkingLot mapRow(@org.springframework.lang.NonNull ResultSet rs, int rowNum) throws SQLException{

        ResultSetMetaData metaData = rs.getMetaData();
        int length = metaData.getColumnCount();

        boolean hasId = false;
        boolean hasName = false;
        boolean hasAddress = false;
        boolean hasLandmark = false;
        boolean hasOpeningTime = false;
        boolean hasClosingTime = false;
        boolean hasSpaceInOut = false;
        boolean hasPrice = false;
        boolean hasWidthLimit = false;
        boolean hasHeightLimit = false;
        boolean hasLat = false;
        boolean hasLng = false;
        boolean hasImages = false;
        boolean hasSquares = false;

        for (int i = 1; i <= length; i++) {
            String columnName = metaData.getColumnName(i);
            
            if (columnName.equals("id")) {
                hasId = true;
            }
            if (columnName.equals("name")) {
                hasName = true;
            }
            if (columnName.equals("address")) {
                hasAddress = true;
            }
            if (columnName.equals("landmark")) {
                hasLandmark = true;
            }
            if (columnName.equals("openingTime")) {
                hasOpeningTime = true;
            }
            if (columnName.equals("closingTime")) {
                hasClosingTime = true;
            }
            if (columnName.equals("spaceInOut")) {
                hasSpaceInOut = true;
            }
            if (columnName.equals("price")) {
                hasPrice = true;
            }
            if (columnName.equals("widthLimit")) {
                hasWidthLimit = true;
            }
            if (columnName.equals("heightLimit")) {
                hasHeightLimit = true;
            }
            if (columnName.equals("lat")) {
                hasLat = true;
            }
            if (columnName.equals("lng")) {
                hasLng = true;
            }
            if (columnName.equals("images")) {
                hasImages = true;
            }
            if (columnName.equals("squares")) {
                hasSquares = true;
            }
        }

        ParkingLot parkinglot = new ParkingLot();

        if (hasId) {
            parkinglot.setParkingLotId(rs.getInt("id"));
        }

        if (hasName) {
            parkinglot.setName(rs.getString("name"));
        }

        if (hasAddress) {
            parkinglot.setAddress(rs.getString("address"));
        }

        if (hasLandmark) {
            parkinglot.setNearLandmark(rs.getString("landmark"));
        }

        if (hasOpeningTime) {
            parkinglot.setOpeningTimeAm(rs.getString("openingTime"));
        }

        if (hasClosingTime) {
            parkinglot.setOpeningTimePm(rs.getString("closingTime"));
        }

        if (hasSpaceInOut) {
            parkinglot.setSpaceInOut(rs.getString("spaceInOut"));
        }

        if (hasPrice) {
            parkinglot.setPrice(rs.getInt("price"));
        }

        if (hasWidthLimit) {
            parkinglot.setCarWidth(rs.getString("widthLimit"));
        }

        if (hasHeightLimit) {
            parkinglot.setCarHeight(rs.getString("heightLimit"));
        }
        
        if (hasLat) {
            parkinglot.setLatitude(rs.getString("lat"));
        }

        if (hasLng) {
            parkinglot.setLongitude(rs.getString("lng"));
        }
        
        if (hasImages) {
            String imgUrlConcat = rs.getString("images");
            if (imgUrlConcat != null && !imgUrlConcat.isEmpty()) {
                String[] imgUrl = imgUrlConcat.split(",");
                parkinglot.setImgUrl(imgUrl);
            }
        }
        
        if (hasSquares) {
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
        }
        
        return parkinglot;
    }
}

