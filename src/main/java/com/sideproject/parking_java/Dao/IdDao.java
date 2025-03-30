package com.sideproject.parking_java.dao;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.exception.InternalServerError;
import com.sideproject.parking_java.model.Member;
import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.utility.MemberRowMapper;
import com.sideproject.parking_java.utility.ParkingLotRowMapper;

@Component
public class IdDao{

    @Autowired 
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int getParkingLotDataIdByMemberId(int memberId) throws InternalServerError{
        String sql = "SELECT * FROM parkinglotdata WHERE member_id = :member_id ORDER BY id DESC LIMIT 1";

        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);

        ParkingLot parkingLotDataId = namedParameterJdbcTemplate.queryForObject(sql, map, new ParkingLotRowMapper());

        if (parkingLotDataId == null) {
            throw new InternalServerError("ParkingLotDataId not found: " + memberId);
        }

        return parkingLotDataId.getParkingLotId();
    }

    public int getMemberIdByParkingLotDataId(int parkingLotDataId) throws InternalServerError{
        String sql = "SELECT member_id FROM parkinglotdata WHERE id = :id";

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", parkingLotDataId);

        Member memberId = namedParameterJdbcTemplate.queryForObject(sql, map, new MemberRowMapper());

        if (memberId == null) {
            throw new InternalServerError("MemberId not found: " + parkingLotDataId);
        }

        return memberId.getId();
    }
}
