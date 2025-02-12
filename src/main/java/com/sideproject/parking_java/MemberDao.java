package com.sideproject.parking_java;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.Exception.DatabaseError;
import com.sideproject.parking_java.Model.Member;

@Component
public class MemberDao {
    
    @Autowired 
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Integer postMemberDao(Member member) {
        if(member == null) {
            throw new DatabaseError("member is null");
        }

        String sql = "INSERT INTO member(account, password, email, creatTime) VALUES (:account, :password, :email, :creatTime)";
        // LocalDateTime creatTime = LocalDateTime.now().withNano(0);       
        HashMap<String, Object> map = new HashMap<>();
        map.put("account", member.getAccount());
        map.put("password", member.getPassword());
        map.put("email", member.getEmail());
        map.put("creatTime", member.getCreatTime());

        int insertId = namedParameterJdbcTemplate.update(sql, map);
        
        if (insertId == 0) {
            throw new DatabaseError("insert failed");
        }
        return insertId;
    }
}
