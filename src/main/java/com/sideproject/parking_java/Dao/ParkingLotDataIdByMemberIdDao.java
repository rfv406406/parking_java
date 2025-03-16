package com.sideproject.parking_java.dao;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.exception.InternalServerError;
import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.utility.ParkingLotRowMapper;

@Component
public class ParkingLotDataIdByMemberIdDao{

    @Autowired 
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int getParkingLotDataIdByMemberId(int memberId) throws InternalServerError{
        String sql = "SELECT * FROM parkinglotdata WHERE member_id = :member_id ORDER BY id DESC LIMIT 1";

        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);

        ParkingLot parkingLotDataId = namedParameterJdbcTemplate.queryForObject(sql, map, new ParkingLotRowMapper());

        if (parkingLotDataId == null) {
            throw new InternalServerError("No parking lot found for memberId: "+memberId);
        }
        return parkingLotDataId.getParkingLotId();
    }
}
