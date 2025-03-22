package com.sideproject.parking_java.dao;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TappayDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int postTappayInsertTransactionsDao(String orderNumber, int memberId, int depositAccountId, int deposit) {
        String sql = "INSERT INTO transactions SET order_number = :order_number, member_id = :member_id, deposit_account_id = :deposit_account_id, transactions_type = :transactions_type, deposit = :deposit, status = :status";

        HashMap<String, Object> map = new HashMap<>();
        map.put("order_number", orderNumber);
        map.put("member_id", memberId);
        map.put("deposit_account_id", depositAccountId);
        map.put("transactions_type", "DEPOSIT");
        map.put("deposit", deposit);
        map.put("status", "未付款");
        
        int insertId = namedParameterJdbcTemplate.update(sql, map);   
        return insertId;
    }

    public void postTappayUpdateStatusDao(String orderNumber, int depositAccountId) {
        String sql = "UPDATE transactions SET status = :status WHERE order_number = :order_number AND deposit_account_id = :deposit_account_id";

        HashMap<String, Object> map = new HashMap<>();
        map.put("order_number", orderNumber);
        map.put("deposit_account_id", depositAccountId);
        map.put("status", "已付款");

        namedParameterJdbcTemplate.update(sql, map);
    }

    public void postTappayUpdateBalanceDao(int memberId, int deposit) {
        String sql = "UPDATE deposit_account SET balance = balance + :balance WHERE member_id = :member_id";

        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
        map.put("balance", deposit);
        
        namedParameterJdbcTemplate.update(sql, map);
    }
}
