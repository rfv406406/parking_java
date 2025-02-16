package com.sideproject.parking_java.Utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.sideproject.parking_java.Model.Member;

public class MemberRowMapper implements RowMapper<Member>{
    @Override
    public Member mapRow(ResultSet rs, int rowNum) throws SQLException{
        Member member = new Member();
        member.setId(rs.getInt("id"));
        member.setAccount(rs.getString("account"));
        member.setEmail(rs.getString("email"));
        return member;
    }
}
