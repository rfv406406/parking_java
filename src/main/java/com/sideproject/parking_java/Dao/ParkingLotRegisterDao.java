package com.sideproject.parking_java.Dao;

import java.util.HashMap;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.sideproject.parking_java.Exception.InternalServerError;
import com.sideproject.parking_java.Model.CarSpaceNumber;
import com.sideproject.parking_java.Model.MemberDetails;
import com.sideproject.parking_java.Model.ParkingLot;
import com.sideproject.parking_java.Utility.ParkingLotRowMapper;

import io.micrometer.common.lang.NonNull;

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
        int memberId = memberDetails.getId();

        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
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
        
        int insertId = namedParameterJdbcTemplate.update(sql, map);

        int parkingLotDataId = getParkingLotDataIdByMemberId(memberId);
        input_parkinglotimages(parkingLot, parkingLotDataId);

        inputParkingLotSquare(parkingLot, parkingLotDataId);
        // System.out.println("parkingLotDataId: "+ parkingLotDataId);        

        return insertId;
    }

    public int getParkingLotDataIdByMemberId(@NonNull int memberId) throws InternalServerError{
        String sql = "SELECT id FROM parkinglotdata WHERE member_id = :member_id ORDER BY id DESC LIMIT 1";

        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);

        ParkingLot parkingLotDataId = namedParameterJdbcTemplate.queryForObject(sql, map, new ParkingLotRowMapper());

        if (parkingLotDataId == null) {
            throw new InternalServerError("No parking lot found for memberId: "+memberId);
        }
        return parkingLotDataId.getId();
    }

    public void input_parkinglotimages(ParkingLot parkingLot, int parkingLotDataId) {
        for (MultipartFile img : parkingLot.getImg()) {
            String fileName = img.getOriginalFilename();
            if(fileName == null || fileName.endsWith("jpg") || fileName.endsWith("jpeg") ||
            fileName.endsWith("png") || fileName.endsWith("jfif")) {
                String UniquefileName = UUID.randomUUID().toString() + fileName;
                // s3_client.upload_fileobj(image, BUCKET_NAME, filename)
                String imgUrl = "https://d1hxt3hn1q2xo2.cloudfront.net/" + UniquefileName;
                String sql = "INSERT INTO parkinglotimage(parkinglotdata_id, image) VALUES (:parkinglotdata_id, :image)";
                HashMap<String, Object> map = new HashMap<>();
                map.put("parkinglotdata_id", parkingLotDataId);
                map.put("image", imgUrl);
                int insertId = namedParameterJdbcTemplate.update(sql, map);
            }
        }
    }

    public void inputParkingLotSquare(ParkingLot parkingLot, int parkingLotDataId) {
        for (CarSpaceNumber carSpaceNumber : parkingLot.getCarSpaceNumber()) {
            String sql = "INSERT INTO parkinglotsquare(parkinglotdata_id, square_number, status) VALUES(:parkinglotdata_id, :square_number, :status)";
            HashMap<String, Object> map = new HashMap<>();
            map.put("parkinglotdata_id", parkingLotDataId);
            map.put("square_number", carSpaceNumber.getValue());
            map.put("status", "閒置中");
            int insertId = namedParameterJdbcTemplate.update(sql, map);
            System.out.println("parkingLot: "+ carSpaceNumber.getName());
        }
    }
}
