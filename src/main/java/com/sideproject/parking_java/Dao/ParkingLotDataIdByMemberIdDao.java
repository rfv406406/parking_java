package com.sideproject.parking_java.Dao;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.Exception.InternalServerError;
import com.sideproject.parking_java.Model.ParkingLot;
import com.sideproject.parking_java.utility.ParkingLotRowMapper;

import io.micrometer.common.lang.NonNull;

@Component
public class ParkingLotDataIdByMemberIdDao{

    @Autowired 
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int getParkingLotDataIdByMemberId(@NonNull int memberId) throws InternalServerError{
        String sql = "SELECT id FROM parkinglotdata WHERE member_id = :member_id ORDER BY id DESC LIMIT 1";

        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);

        ParkingLot parkingLotDataId = namedParameterJdbcTemplate.queryForObject(sql, map, new ParkingLotRowMapper());

        if (parkingLotDataId == null) {
            throw new InternalServerError("No parking lot found for memberId: "+memberId);
        }
        return parkingLotDataId.getParkingLotId();
    }
}
