package com.sideproject.parking_java.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.sideproject.parking_java.model.Member;

public class MemberRowMapper implements RowMapper<Member>{
    @Override
    public Member mapRow(@org.springframework.lang.NonNull ResultSet rs, int rowNum) throws SQLException{
        Member member = new Member();

        member.setId(rs.getInt("id"));
        member.setAccount(rs.getString("account"));
        member.setPassword(rs.getString("password"));
        member.setEmail(rs.getString("email"));
        member.setRole(rs.getString("role"));
        member.setName(rs.getString("name"));
        member.setBirthday(rs.getString("birthday"));
        member.setCellphone(rs.getString("cellphone"));
        member.setCreateTime(rs.getString("create_time"));
        member.setLastLogInTime(rs.getString("last_log_in_time"));
        member.setStatus(rs.getString("status"));
        return member;
    }
}
