package com.demo.pojo;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User
{
    private String userName;
    private String password;
    private Date createdOn;
    private String role;
    private String uid;
    private List<AccessToken> tokens;

    private Person person;

    public User()
    {
        this.person = new Person();
        this.createdOn = new Date();
        this.tokens = new ArrayList<>();
    }

    public User(String userName, String password) {
        super();
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public List<AccessToken> getTokens() {
        return tokens;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", role='" + role + '\'' +
                ", createdOn=" + createdOn +
                ", password='" + password + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
