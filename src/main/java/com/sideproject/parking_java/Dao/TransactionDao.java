package com.sideproject.parking_java.dao;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.exception.InvalidParameterError;
import com.sideproject.parking_java.model.CarSpaceNumber;
import com.sideproject.parking_java.model.Transaction;
import com.sideproject.parking_java.utility.TransactionRowMapper;

@Component
public class TransactionDao {

    @Autowired 
    JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public int postInsertTransactionDao(int memberId, int depositAccountId, String orderNumber, Transaction transaction) {
        String sql = "INSERT INTO transactions SET member_id = :member_id, deposit_account_id = :deposit_account_id, car_id = :car_id, order_number = :order_number, "
        + "parkinglot_id = :parkinglot_id, parkinglot_name = (SELECT name FROM parkinglotdata WHERE id = :parkinglot_id), "
        + "parkinglotsquare_id = :parkinglotsquare_id, parkinglotsquare_number = (SELECT square_number FROM parkinglotsquare WHERE id = :parkinglotsquare_id), "
        + "starttime = :starttime, transactions_type = :transactions_type, amount = :amount, status = :status";

        HashMap<String, Object> map = new HashMap<>();

        map.put("member_id", memberId);
        map.put("deposit_account_id", depositAccountId);
        map.put("car_id", transaction.getCarId());
        map.put("order_number", orderNumber);
        map.put("parkinglot_id", transaction.getParkingLotId());
        map.put("parkinglotsquare_id", transaction.getParkingLotSquareId());
        map.put("starttime", transaction.getStartTime());
        map.put("transactions_type", transaction.getTransactionType());
        map.put("status", transaction.getStatus());
        map.put("amount", transaction.getAmount());
        
        int insertId = namedParameterJdbcTemplate.update(sql, map);

        return insertId;
    }

    public void putUpdateParkingLotSquareStatusDao(Transaction transaction) {
        System.out.println("transaction.getParkingLotSquareId(): "+transaction.getParkingLotSquareId());
        String aqlS = "SELECT status FROM parkinglotsquare WHERE id = ?"; 
        String statusChecking = jdbcTemplate.queryForObject(aqlS, String.class, transaction.getParkingLotSquareId());
        System.out.println("statusChecking: "+statusChecking);

        if ("使用中".equals(statusChecking) && "未付款".equals(transaction.getStatus())) {
            throw new InvalidParameterError("CarSpaceNumber is using");
        }
        
        String sql = "UPDATE parkinglotsquare SET status = :status WHERE id = :parkinglotsquare_id";
        List<CarSpaceNumber> carSpaceNumber = transaction.getParkingLot().getCarSpaceNumber();
        String status = carSpaceNumber.get(0).getStatus();
        HashMap<String, Object> map = new HashMap<>();

        map.put("parkinglotsquare_id", transaction.getParkingLotSquareId());
        map.put("status", status);

        namedParameterJdbcTemplate.update(sql, map);
    }

    public Transaction getUnpaidTransactionDao(int memberId) {
        String sql = "SELECT t.id, t.order_number, t.starttime, t.parkinglotsquare_id, p.*, s.* " + 
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

    public int putUpdateParkingLotUsageDao(int memberId, String orderNumber, Transaction transaction) {
        String sql = "UPDATE transactions SET stoptime = :stoptime, amount = :amount, status = :status, " +
        "transactions_time = CURRENT_TIMESTAMP WHERE member_id = :member_id AND order_number = :order_number AND transactions_type = :transactions_type";

        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
        map.put("order_number", orderNumber);
        map.put("stoptime", transaction.getStopTime());
        map.put("amount", transaction.getAmount());
        map.put("status", transaction.getStatus());
        map.put("transactions_type", transaction.getTransactionType());
        int insertNumber = namedParameterJdbcTemplate.update(sql, map);
        return insertNumber;
    }

    public void putUpdateBalanceDao(int memberId, int amount) {
        String sql = "UPDATE deposit_account SET balance = balance + :balance WHERE member_id = :member_id";

        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);
        map.put("balance", amount);
        
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
