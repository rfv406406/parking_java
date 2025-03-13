package com.sideproject.parking_java.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.sideproject.parking_java.model.MemberDepositAccount;

public class MemberDepositAccountRowMapper implements RowMapper<MemberDepositAccount>{
    @Override
    public MemberDepositAccount mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
        MemberDepositAccount memberDepositAccount = new MemberDepositAccount();

        memberDepositAccount.setDepositAccountId(rs.getInt("id"));

        return memberDepositAccount;
    }
}
