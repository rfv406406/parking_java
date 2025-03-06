package com.sideproject.parking_java.Dao;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.Model.CarSpaceNumber;
import com.sideproject.parking_java.Model.MemberDetails;
import com.sideproject.parking_java.Model.ParkingLot;

@Component
public class ParkingLotRegisterDao {

    @Autowired 
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int postParkingLotRegisterDao(ParkingLot parkingLot) {
        String sql = "INSERT INTO parkinglotdata(member_id, name, address, landmark, openingTime, closingTime, spaceInOut, price, widthLimit, heightLimit, lat, lng)" 
        + "VALUES (:member_id, :name, :address, :landmark, :openingTime, :closingTime, :spaceInOut, :price, :widthLimit, :heightLimit, :lat, :lng)";

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication auth = context.getAuthentication();
        Object principal = auth.getPrincipal();
        MemberDetails memberDetails = (MemberDetails)principal;
        int id = memberDetails.getId();

        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", id);
        map.put("name", parkingLot.getName());
        map.put("address", parkingLot.getAddress());
        map.put("landmark", parkingLot.getNearLandmark());
        map.put("openingTime", parkingLot.getOpeningTimeAm());
        map.put("closingTime", parkingLot.getOpeningTimePm());
        map.put("spaceInOut", parkingLot.getSpaceInOut());
        map.put("price", parkingLot.getPrice());
        map.put("widthLimit", parkingLot.getCarWidth());
        map.put("heightLimit", parkingLot.getCarHeight());
        map.put("lat", parkingLot.getLatitude());
        map.put("lng", parkingLot.getLongitude());
        for (CarSpaceNumber carSpaceNumber : parkingLot.getCarSpaceNumber()) {
            System.out.println("parkingLot: "+ carSpaceNumber.getName());
        }
        int insertId = namedParameterJdbcTemplate.update(sql, map);

        return insertId;
    }
}
