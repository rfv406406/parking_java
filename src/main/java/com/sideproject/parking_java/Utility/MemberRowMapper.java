package com.sideproject.parking_java.utility;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.sideproject.parking_java.model.Member;

public class MemberRowMapper implements RowMapper<Member>{
    @Override
    public Member mapRow(@org.springframework.lang.NonNull ResultSet rs, int rowNum) throws SQLException{
        ResultSetMetaData metaData = rs.getMetaData();
        int length = metaData.getColumnCount();

        Member member = new Member();

        for (int i=1; i<=length; i++) {
            String columnName = metaData.getColumnName(i);

            if (columnName.equals("id")) {
                member.setId(rs.getInt("id"));
            }
            // memberId get by parkinglotId 
            if (columnName.equals("member_id")) {
                member.setId(rs.getInt("member_id"));
            }
            if (columnName.equals("account")) {
                member.setAccount(rs.getString("account"));
            }
            if (columnName.equals("password")) {
                member.setPassword(rs.getString("password"));
            }
            if (columnName.equals("email")) {
                member.setEmail(rs.getString("email"));
            }
            if (columnName.equals("role")) {
                member.setRole(rs.getString("role"));
            }
            if (columnName.equals("name")) {
                member.setName(rs.getString("name"));
            }
            if (columnName.equals("birthday")) {
                member.setName(rs.getString("birthday"));
            }
            if (columnName.equals("cellphone")) {
                member.setName(rs.getString("cellphone"));
            }
            if (columnName.equals("create_time")) {
                member.setName(rs.getString("create_time"));
            }
            if (columnName.equals("last_log_in_time")) {
                member.setName(rs.getString("last_log_in_time"));
            }
            if (columnName.equals("status")) {
                member.setName(rs.getString("status"));
            }
        }
        
        return member;
    }
}
