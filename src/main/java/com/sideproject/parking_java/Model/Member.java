package com.sideproject.parking_java.Model;

import java.util.Date;

import com.sideproject.parking_java.Utility.TimeFormat;

// @AllArgsConstructor
public class Member {
    private final String account;
    private final String password;
    private final String email;
    private final String creatTime;

    public Member(String account, String password, String email) {
        this.account = account;
        this.password = password;
        this.email = email;
        this.creatTime = TimeFormat.timeFormat(new Date());
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
