package com.sideproject.parking_java.dao;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.model.Transaction;
import com.sideproject.parking_java.utility.TransactionRowMapper;

@Component
public class TransactionDao {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int postInsertTransactionDao(int memberId, int depositAccountId, String orderNumber, Transaction transcation) {
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
        map.put("transactions_type", transcation.getTransactionType());
        map.put("status", transcation.getStatus());
        map.put("amount", transcation.getAmount());

        int insertId = namedParameterJdbcTemplate.update(sql, map);

        return insertId;
    }

    public void putUpdateParkingLotSquareStatusDao(Transaction transcation) {
        String sql = "UPDATE parkinglotsquare SET status = :status WHERE id = :parkinglotsquare_id";
        
        HashMap<String, Object> map = new HashMap<>();

        map.put("parkinglotsquare_id", transcation.getParkingLotSquareId());
        map.put("status", "使用中");

        namedParameterJdbcTemplate.update(sql, map);
    }

    public void putUpdateMemberStatusDao(int memberId) {
        String sql = "UPDATE member SET status = :status WHERE id = :member_id";

        HashMap<String, Object> map = new HashMap<>();

        map.put("member_id", memberId);
        map.put("status", "停車中");

        namedParameterJdbcTemplate.update(sql, map);
    }

    public Transaction getUnpaidTransactionDao(int memberId) {
        String sql = "SELECT t.id, t.order_number, t.starttime, p.*, s.* " + 
                     "FROM transactions t " + 
                     "LEFT JOIN parkinglotdata p ON t.parkinglot_id = p.id " + 
                     "LEFT JOIN parkinglotsquare s ON t.parkinglotsquare_id = s.id " +
                     "WHERE t.member_id = :member_id AND stoptime IS NULL AND transactions_type = 'CONSUMPTION' AND t.status = '未付款'";
                     
        HashMap<String, Object> map = new HashMap<>();

        map.put("member_id", memberId);
        List<Transaction> unpaidParkingLotUasgeData = namedParameterJdbcTemplate.query(sql, map, new TransactionRowMapper());

        if (!unpaidParkingLotUasgeData.isEmpty()) {
            return unpaidParkingLotUasgeData.get(0);
        } else {
            return null;
        }
    }

    public void putUpdateParkingLotUsageDao(int memberId, String orderNumber, Transaction transcation) {
        String sql = "UPDATE transactions SET stoptime = :stoptime, amount = :amount, status = :status, transactions_time = CURRENT_TIMESTAMP WHERE member_id = :member_id AND order_number = :order_number";

        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
        map.put("order_number", orderNumber);
        map.put("stoptime", transcation.getStopTime());
        map.put("amount", transcation.getAmount());
        map.put("status", transcation.getStatus());

        namedParameterJdbcTemplate.update(sql, map);
    }

    public void putUpdateBalanceDao(int memberId, int deposit) {
        String sql = "UPDATE deposit_account SET balance = balance + :balance WHERE member_id = :member_id";

        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
        map.put("balance", deposit);
        
        namedParameterJdbcTemplate.update(sql, map);
    }

    public void putTappayUpdateStatusDao(String orderNumber, int depositAccountId, String transactionsTime) {
        String sql = "UPDATE transactions SET status = :status, transactions_time = :transactions_time WHERE order_number = :order_number AND deposit_account_id = :deposit_account_id";

        HashMap<String, Object> map = new HashMap<>();
        map.put("order_number", orderNumber);
        map.put("deposit_account_id", depositAccountId);
        map.put("status", "已付款");
        map.put("transactions_time", transactionsTime);

        namedParameterJdbcTemplate.update(sql, map);
    }

    public List<Transaction> getTransactionRecordsDao(int memberId) {
        String sql = "SELECT t.*, p.*, s.*, c.* " + 
                     "FROM transactions t " + 
                     "LEFT JOIN parkinglotdata p ON t.parkinglot_id = p.id " +
                     "LEFT JOIN parkinglotsquare s ON t.parkinglotsquare_id = s.id " +
                     "LEFT JOIN car c ON t.car_id = c.id " +
                     "WHERE t.member_id = :member_id";

        HashMap<String, Object> map = new HashMap<>();

        map.put("member_id", memberId);

        List<Transaction> transactionRecords = namedParameterJdbcTemplate.query(sql, map, new TransactionRowMapper());

        return transactionRecords;
    }
}
