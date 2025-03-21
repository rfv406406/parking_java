package com.sideproject.parking_java.dao;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.model.Transaction;

@Component
public class ParkingLotUsingDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public void postParkingLotPaymentDao(int memberId, int depositAccountId, String orderNumber, Transaction transcation) {
        String sql = "INSERT INTO transactions SET member_id = :member_id, deposit_account_id = :deposit_account_id, car_id = :car_id, order_number = :order_number, "
        + "parkinglot_id = :parkinglot_id, parkinglotsquare_id = :parkinglotsquare_id, starttime = :starttime, transactions_type = :transactions_type, status = :status";

        HashMap<String, Object> map = new HashMap<>();

        map.put("member_id", memberId);
        map.put("deposit_account_id", depositAccountId);
        map.put("car_id", transcation.getCarId());
        map.put("order_number", orderNumber);
        map.put("parkinglot_id", transcation.getParkingLotId());
        map.put("parkinglotsquare_id", transcation.getParkingLotSquareId());
        map.put("starttime", transcation.getStartTime());
        map.put("transactions_type", "CONSUMPTION");
        map.put("status", "未付款");

        namedParameterJdbcTemplate.update(sql, map);
    }

    public void postParkingLotSquareStatusDao(Transaction transcation) {
        String sql = "UPDATE parkinglotsquare SET status = :status WHERE id = :parkinglotsquare_id";
        
        HashMap<String, Object> map = new HashMap<>();

        map.put("parkinglotsquare_id", transcation.getParkingLotSquareId());
        map.put("status", "使用中");

        namedParameterJdbcTemplate.update(sql, map);
    }

    public void postMemberStatusDao(int memberId) {
        String sql = "UPDATE member SET status = :status WHERE id = :member_id";

        HashMap<String, Object> map = new HashMap<>();

        map.put("member_id", memberId);
        map.put("status", "停車中");

        namedParameterJdbcTemplate.update(sql, map);
    }
}
