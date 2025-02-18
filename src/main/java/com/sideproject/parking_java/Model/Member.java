package com.sideproject.parking_java.Model;

import java.util.Date;

import com.sideproject.parking_java.Utility.TimeFormat;

// @AllArgsConstructor
public class Member {
    private int id;
    private String account;
    private String password;
    private String email;
    private String creatTime = TimeFormat.timeFormat(new Date());

    public Member() {};
    //sign in
    // public Member(String account, String password, String email) {
    //     this.account = account;
    //     this.password = password;
    //     this.email = email;
    //     this.creatTime = TimeFormat.timeFormat(new Date());
    // }
    //MemberRowMapper
    public Member(String account) {
        this.account = account;
    }

    public void setId(int id) {
        this.id =  id;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // public void setCreatTime() {
    //     this.creatTime = TimeFormat.timeFormat(new Date());
    // }

    public int getId() {
        return id;
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
