package com.sideproject.parking_java.utility;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.jdbc.core.RowMapper;

import com.sideproject.parking_java.model.CarSpaceNumber;
import com.sideproject.parking_java.model.Member;
import com.sideproject.parking_java.model.ParkingLot;

public class ParkingLotRowMapper implements RowMapper<ParkingLot>{
    @Override
    public ParkingLot mapRow(@org.springframework.lang.NonNull ResultSet rs, int rowNum) throws SQLException{

        ResultSetMetaData metaData = rs.getMetaData();
        int length = metaData.getColumnCount();

        boolean hasOwnerId = false;
        boolean hasOwnerAccount = false;
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
        boolean hasSquareNumber = false;

        for (int i = 1; i <= length; i++) {
            String columnName = metaData.getColumnName(i);

            if (columnName.equals("member_id")) {
                hasOwnerId = true;
            }

            if (columnName.equals("account")) {
                hasOwnerAccount = true;
            }
            
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
            if (columnName.equals("opening_time")) {
                hasOpeningTime = true;
            }
            if (columnName.equals("closing_time")) {
                hasClosingTime = true;
            }
            if (columnName.equals("space_in_out")) {
                hasSpaceInOut = true;
            }
            if (columnName.equals("price")) {
                hasPrice = true;
            }
            if (columnName.equals("width_limit")) {
                hasWidthLimit = true;
            }
            if (columnName.equals("height_limit")) {
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
            if (columnName.equals("square_number")) {
                hasSquareNumber = true;
            }
        }

        ParkingLot parkinglot = new ParkingLot();
        Member member = new Member();

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
            parkinglot.setOpeningTime(rs.getString("opening_time"));
        }

        if (hasClosingTime) {
            parkinglot.setClosingTime(rs.getString("closing_time"));
        }

        if (hasSpaceInOut) {
            parkinglot.setSpaceInOut(rs.getString("space_in_out"));
        }

        if (hasPrice) {
            parkinglot.setPrice(rs.getInt("price"));
        }

        if (hasWidthLimit) {
            parkinglot.setCarWidth(rs.getString("width_limit"));
        }

        if (hasHeightLimit) {
            parkinglot.setCarHeight(rs.getString("height_limit"));
        }
        
        if (hasLat) {
            parkinglot.setLatitude(rs.getDouble("lat"));
        }

        if (hasLng) {
            parkinglot.setLongitude(rs.getDouble("lng"));
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

                for (int i=0; i<carSpaceNumberConcatToStringArray.length; i=i+4) {
                    CarSpaceNumber carSpaceNumber = new CarSpaceNumber();
                    carSpaceNumber.setId(Integer.valueOf(carSpaceNumberConcatToStringArray[i]));
                    carSpaceNumber.setParkingLotId(Integer.valueOf(carSpaceNumberConcatToStringArray[i+1]));
                    carSpaceNumber.setValue(carSpaceNumberConcatToStringArray[i+2]);
                    carSpaceNumber.setStatus(carSpaceNumberConcatToStringArray[i+3]);
                    carSpaceNumberConcat.add(carSpaceNumber);
                }

                parkinglot.setCarSpaceNumber(carSpaceNumberConcat);
            }
        }

        if (hasSquareNumber) {
            ArrayList<CarSpaceNumber> carSpaceNumberArray = new ArrayList<>();
            CarSpaceNumber carSpaceNumber = new CarSpaceNumber();
            carSpaceNumber.setParkingLotId(rs.getInt("parkinglot_id"));
            carSpaceNumber.setValue(rs.getString("square_number"));
            carSpaceNumberArray.add(carSpaceNumber);
            parkinglot.setCarSpaceNumber(carSpaceNumberArray);
        }

        if (hasOwnerId) {
            member.setId(rs.getInt("member_id"));
        }

        if (hasOwnerAccount) {
            member.setAccount(rs.getString("account"));
        }

        parkinglot.setMember(member);
        
        return parkinglot;
    }
}

