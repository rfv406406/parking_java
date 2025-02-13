package com.sideproject.parking_java.Utility;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.tree.RowMapper;

import com.sideproject.parking_java.Model.Member;

public class MemberRowMapper implements RowMapper<Member>{
    @Override
    public Member mapRow(ResultSet rs, int rowNum) throws SQLException{
        Member member = new Member(rs.getAccount());
        return member;
    }
}
