package com.sideproject.parking_java.utility;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.jdbc.core.RowMapper;

import com.sideproject.parking_java.model.Car;
import com.sideproject.parking_java.model.CarSpaceNumber;
import com.sideproject.parking_java.model.ParkingLot;
import com.sideproject.parking_java.model.Transaction;

public class TransactionRowMapper implements RowMapper<Transaction>{
    @Override
    public Transaction mapRow(@org.springframework.lang.NonNull ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int length = metaData.getColumnCount();

        Transaction transaction = new Transaction();
        ParkingLot parkingLot = new ParkingLot();
        Car car = new Car();
        ArrayList<CarSpaceNumber> carSpaceNumberConcat = new ArrayList<>();
        CarSpaceNumber carSpaceNumber = new CarSpaceNumber();

        for (int i=1; i<=length; i++) {

            String columnName = metaData.getColumnName(i);
            if (columnName.equals("id")) {
                transaction.setId(rs.getInt("id"));
            }
            if (columnName.equals("order_number")) {
                transaction.setOrderNumber(rs.getString("order_number"));
            }
            if (columnName.equals("member_id")) {
                transaction.setMemberId(rs.getInt("member_id"));
            }
            if (columnName.equals("car_id")) {
                transaction.setCarId(rs.getInt("car_id"));
            }
            if (columnName.equals("carboard_number")) {
                car.setCarNumber(rs.getString("carboard_number"));
            }
            if (columnName.equals("deposit_account_id")) {
                transaction.setDepositAccountId(rs.getInt("deposit_account_id"));
            }
            if (columnName.equals("parkinglot_id")) {
                transaction.setParkingLotId(rs.getInt("parkinglot_id"));
            }
            if (columnName.equals("parkinglot_name")) {
                transaction.setParkingLotName(rs.getString("parkinglot_name"));
            }
            if (columnName.equals("parkinglotsquare_id")) {
                transaction.setParkingLotSquareId(rs.getInt("parkinglotsquare_id"));
            }
            if (columnName.equals("parkinglotsquare_number")) {
                transaction.setParkingLotSquareNumber(rs.getInt("parkinglotsquare_number"));
            }
            if (columnName.equals("starttime")) {
                transaction.setStartTime(rs.getString("starttime"));
            }
            if (columnName.equals("stoptime")) {
                transaction.setStopTime(rs.getString("stoptime"));
            }
            if (columnName.equals("transactions_type")) {
                transaction.setTransactionType(rs.getString("transactions_type"));
            }
            if (columnName.equals("amount")) {
                transaction.setAmount(rs.getInt("amount"));
            }
            if (columnName.equals("status")) {
                transaction.setStatus(rs.getString("status"));
            }
            if (columnName.equals("transactions_time")) {
                transaction.setTransactionsTime(rs.getString("transactions_time"));
            }
            if (columnName.equals("square_number")) {
                carSpaceNumber.setValue(rs.getString("square_number"));
                carSpaceNumberConcat.add(carSpaceNumber);
                parkingLot.setCarSpaceNumber(carSpaceNumberConcat);
            }
            if (columnName.equals("name")) {
                parkingLot.setName(rs.getString("name"));
            }
            if (columnName.equals("address")) {
                parkingLot.setAddress(rs.getString("address"));
            }
            if (columnName.equals("price")) {
                parkingLot.setPrice(rs.getInt("price"));
            }
        }
        transaction.setCar(car);
        transaction.setParkingLot(parkingLot);
       
        return transaction;
    }
}
