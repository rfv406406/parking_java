package com.sideproject.parking_java.Model;

import java.util.Date;

import com.sideproject.parking_java.Utility.TimeFormat;

// @AllArgsConstructor
public class Member {
    private String account;
    private String password;
    private String email;
    private String creatTime;

    public Member(String account, String password, String email) {
        this.account = account;
        this.password = password;
        this.email = email;
        this.creatTime = TimeFormat.timeFormat(new Date());
    }
    //MemberRowMapper
    public Member(String account) {
        this.account = account;
    }

    public String getAccount() {
        return account;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getCreatTime() {
        return creatTime;
    }

}
