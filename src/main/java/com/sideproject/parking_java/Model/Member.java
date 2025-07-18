package com.sideproject.parking_java.model;
public class Member {
    private Integer id;
    private String account;
    private String password;
    private String email;
    private String role;
    private String name;
    private String birthday;
    private String cellphone;
    private String createTime;
    private String lastLogInTime;
    private String status;
    private Integer balance;
    
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

    public void setRole(String role) {
        this.role = role;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public void setLastLogInTime(String lastLogInTime) {
        this.lastLogInTime = lastLogInTime;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getId() {
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

    public String getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getCellphone() {
        return cellphone;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getLastLogInTime() {
        return lastLogInTime;
    }

    public String getStatus() {
        return status;
    }

    public Integer getBalance() {
        return balance;
    }
}
