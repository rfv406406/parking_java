package com.sideproject.parking_java;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.Model.Member;

@Component
public class MemberDao {
    
    @Autowired 
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public String insertStudent(Member member) {
        String sql = "INSERT INTO member(account, password, email, creatTime) VALUES (:account, :password, :email, :creatTime)";
        // LocalDateTime creatTime = LocalDateTime.now().withNano(0);       
        HashMap<String, Object> map = new HashMap<>();
        map.put("account", member.getAccount());
        map.put("password", member.getPassword());
        map.put("email", member.getEmail());
        map.put("creatTime", member.getCreatTime());
        //  if (true) {
        //     throw new RuntimeException("123");
        // } 
        namedParameterJdbcTemplate.update(sql, map);
        return "INSERT";
    }
}
