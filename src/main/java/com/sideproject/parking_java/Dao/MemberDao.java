package com.sideproject.parking_java.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.sideproject.parking_java.exception.DatabaseError;
import com.sideproject.parking_java.model.Member;
import com.sideproject.parking_java.model.MemberDepositAccount;
import com.sideproject.parking_java.utility.MemberDepositAccountRowMapper;
import com.sideproject.parking_java.utility.MemberRowMapper;
import com.sideproject.parking_java.utility.TimeUtils;

import jakarta.transaction.Transactional;

@Component
public class MemberDao {
    
    @Autowired 
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Transactional
    public Integer postMemberDao(Member member) throws DatabaseError {
        String sql = "INSERT INTO member(account, password, email, role, create_time) VALUES (:account, :password, :email, :role, :create_time)";
        String sql2 = "INSERT INTO deposit_account(member_id) VALUES (:member_id)";

        HashMap<String, Object> map = new HashMap<>();
        map.put("account", member.getAccount());
        map.put("password", member.getPassword());
        map.put("email", member.getEmail());
        map.put("role", "user");
        member.setCreateTime(TimeUtils.timeFormat(new Date()));
        map.put("create_time", member.getCreateTime());

        int insertId = namedParameterJdbcTemplate.update(sql, map);
        if (insertId == 0) {
            throw new DatabaseError("member registation failed");
        }

        Member returnMemberId = getMemberAuthDao(member.getAccount());

        HashMap<String, Object> map2 = new HashMap<>();
        map2.put("member_id", returnMemberId.getId());

        namedParameterJdbcTemplate.update(sql2, map2);
        
        
        return insertId;
    }

    public Member getMemberAuthDao(String account) {
        String sql = "SELECT id, account, password, role FROM member WHERE account = :account";
        HashMap<String, Object> map = new HashMap<>();
        map.put("account", account);
        Member postGetMemberAuth = namedParameterJdbcTemplate.queryForObject(sql, map, new MemberRowMapper());
        return postGetMemberAuth;
    }

    public boolean getAccountByValueDao(Member value) {
        String sql = "SELECT account FROM member WHERE account = :account";
        HashMap<String, Object> map = new HashMap<>();
        map.put("account", value.getAccount());
        List<Member> getAccountByValue = namedParameterJdbcTemplate.query(sql, map, new MemberRowMapper());
    
        return getAccountByValue.isEmpty();
    }

    public Member getMemberStatusByAccount(String account) {
        String sql = "SELECT status FROM member WHERE account = :account";
        HashMap<String, Object> map = new HashMap<>();
        map.put("account", account);
        Member getMemberStatusByAccount = namedParameterJdbcTemplate.queryForObject(sql, map, new MemberRowMapper());
        return getMemberStatusByAccount;
    }

    public int getDepositAccountIdDao(int memberId) {
        String sql = "SELECT id FROM deposit_account WHERE member_id = :member_id";

        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);

        List<MemberDepositAccount> memberDepositAccount = namedParameterJdbcTemplate.query(sql, map, new MemberDepositAccountRowMapper());

        if (memberDepositAccount.get(0) == null) {
            throw new DatabaseError("memberDepositAccount not found");
        }

        int depositAccountId = memberDepositAccount.get(0).getDepositAccountId();

        return depositAccountId;
    }

    public Member getMemberDetailsByMemberIdDao(int memberId) {
        String sql = "SELECT * FROM member WHERE id = :member_Id";
        HashMap<String, Object> map = new HashMap<>();
        map.put("member_Id", memberId);
        Member memberDetails = namedParameterJdbcTemplate.queryForObject(sql, map, new MemberRowMapper());
        return memberDetails;
    }

    public void putUpdateMemberDetailsDao(int memberId, Member memberDetails) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("member_id", memberId);

        String sql = "UPDATE Member SET ";

        if (memberDetails.getName() != null && !memberDetails.getName().isEmpty()) {
            sql += "name = :name, ";
            map.put("name", memberDetails.getName());
        }

        if (memberDetails.getEmail() != null && !memberDetails.getEmail().isEmpty()) {
            sql += "email = :email, ";
            map.put("email", memberDetails.getEmail());
        }

        if (memberDetails.getBirthday() != null && !memberDetails.getBirthday().isEmpty()) {
            sql += "birthday = :birthday, ";
            map.put("birthday", memberDetails.getBirthday());
        }

        if (memberDetails.getCellphone() != null && !memberDetails.getCellphone().isEmpty()) {
            sql += "cellphone = :cellphone, ";
            map.put("cellphone", memberDetails.getCellphone());
        }

        sql = sql.substring(0, sql.length()-2) + " WHERE id = :member_id";

        namedParameterJdbcTemplate.update(sql, map);
    }
}
