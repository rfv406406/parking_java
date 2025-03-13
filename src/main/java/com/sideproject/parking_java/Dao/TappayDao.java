package com.sideproject.parking_java.dao;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.model.MemberDepositAccount;
import com.sideproject.parking_java.utility.MemberDepositAccountRowMapper;

@Component
public class TappayDao {
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int postGetDepositAccountIdDao(int memberId) {
        String sql1 = "SELECT deposit_account.id FROM deposit_account WHERE member_id = :member_id";

        HashMap<String, Object> map1 = new HashMap<>();
        map1.put("member_id", memberId);

        MemberDepositAccount memberDepositAccount = namedParameterJdbcTemplate.queryForObject(sql1, map1, new MemberDepositAccountRowMapper());
        int depositAccountId = memberDepositAccount.getDepositAccountId();

        return depositAccountId;
    }

    public int postTappayInsertTransactionsDao(String orderNumber, int depositAccountId, int deposit) {
        String sql2 = "INSERT INTO transactions(order_number, deposit_account_id, transactions_type, amount, status) VALUES(:order_number, :deposit_account_id, :transactions_type, :amount, :status)";

        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("order_number", orderNumber);
        map2.put("deposit_account_id", depositAccountId);
        map2.put("transactions_type", "DEPOSIT");
        map2.put("amount", deposit);
        map2.put("status", "未繳款");
        
        int insertId = namedParameterJdbcTemplate.update(sql2, map2);   
        return insertId;
    }

    public void postTappayUpdateStatusDao(String orderNumber, int depositAccountId) {
        String sql3 = "UPDATE transactions SET status = :status WHERE order_number = :order_number AND deposit_account_id = :deposit_account_id";

        HashMap<String, Object> map3 = new HashMap<>();
        map3.put("order_number", orderNumber);
        map3.put("deposit_account_id", depositAccountId);
        map3.put("status", "已繳款");

        namedParameterJdbcTemplate.update(sql3, map3);
    }

    public void postTappayUpdateBalanceDao(int memberId, int deposit) {
        String sql4 = "UPDATE deposit_account SET balance = balance + :balance WHERE member_id = :member_id";

        HashMap<String, Object> map4 = new HashMap<>();
        map4.put("member_id", memberId);
        map4.put("balance", deposit);
        
        namedParameterJdbcTemplate.update(sql4, map4);
    }
}
