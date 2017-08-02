package com.example.serenade.serenade.bean;

/**
 * Created by Serenade on 2017/7/31.
 */

public class User {
    private User(){

    }
    private static class UserHolder {
        public static final User instance = new User();
    }
    public static User getInstance(){
        return UserHolder.instance;
    }
    private String username,account,password,head;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }
}
