package com.sideproject.parking_java.utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.NonNull;

import com.sideproject.parking_java.Model.Member;

public class MemberRowMapper implements RowMapper<Member>{
    @Override
    public Member mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException{
        Member member = new Member();

        member.setId(rs.getInt("id"));
        member.setAccount(rs.getString("account"));
        member.setPassword(rs.getString("password"));
        member.setEmail(rs.getString("email"));
        member.setRole(rs.getString("role"));
        member.setName(rs.getString("name"));
        member.setBirthday(rs.getString("birthday"));
        member.setCellphone(rs.getString("cellphone"));
        member.setCreateTime(rs.getString("createTime"));
        member.setLastLogInTime(rs.getString("lastLogInTime"));
        member.setStatus(rs.getString("status"));
        return member;
    }
}
