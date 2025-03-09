package com.sideproject.parking_java.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.model.Member;
import com.sideproject.parking_java.utility.MemberRowMapper;
import com.sideproject.parking_java.utility.TimeFormat;

@Component
public class MemberDao {
    
    @Autowired 
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public Integer postMemberDao(Member member) throws DatabaseError {
        String sql = "INSERT INTO member(account, password, email, role, createTime) VALUES (:account, :password, :email, :role, :createTime)";
        // LocalDateTime creatTime = LocalDateTime.now().withNano(0);       
        HashMap<String, Object> map = new HashMap<>();
        map.put("account", member.getAccount());
        map.put("password", member.getPassword());
        map.put("email", member.getEmail());
        map.put("role", "user");
        member.setCreateTime(TimeFormat.timeFormat(new Date()));
        map.put("createTime", member.getCreateTime());

        int insertId = namedParameterJdbcTemplate.update(sql, map);
        
        if (insertId == 0) {
            throw new DatabaseError("insert failed");
        }
        return insertId;
    }

    public Member postGetMemberAuthDao(String account) {
        String sql = "SELECT id, account, password, email , name, role, birthday, cellphone, createTime, lastLogInTime, status FROM member WHERE account = :account";
        HashMap<String, Object> map = new HashMap<>();
        map.put("account", account);
        Member postGetMemberAuth = namedParameterJdbcTemplate.queryForObject(sql, map, new MemberRowMapper());
        return postGetMemberAuth;
    }

    public boolean getAccountByValueDao(Member value) {
        String sql = "SELECT id, account, password, email , name, role, birthday, cellphone, createTime, lastLogInTime, status FROM member WHERE account = :account";
        HashMap<String, Object> map = new HashMap<>();
        map.put("account", value.getAccount());
        List<Member> getAccountByValue = namedParameterJdbcTemplate.query(sql, map, new MemberRowMapper());
    
        return getAccountByValue.isEmpty();
    }

    public Member getMemberStatusByAccount(String account) {
        String sql = "SELECT id, account, password, email , name, role, birthday, cellphone, createTime, lastLogInTime, status FROM member WHERE account = :account";
        HashMap<String, Object> map = new HashMap<>();
        map.put("account", account);
        Member getMemberStatusByAccount = namedParameterJdbcTemplate.queryForObject(sql, map, new MemberRowMapper());
        return getMemberStatusByAccount;
    }
}
